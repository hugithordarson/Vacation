package vacation;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import app.VacationCore;
import er.extensions.foundation.ERXUtilities;
import vacation.data.DrivingRoute;
import vacation.data.RouteStop;
import vacation.data.Spot;
import vacation.data.Trip;
import vacation.data.CalendarEvent;
import vacation.data.Visit;

/**
 * Populates the (in-memory) DB from the JSON datasets in woresources on startup.
 * The JSON files remain the editable source of truth until we move to actual-production postgres.
 */

public class SeedData {

	private static final Logger logger = LoggerFactory.getLogger( SeedData.class );

	// DTOs mirroring the JSON structure — dates as ISO strings, relations as slugs
	private record VisitJSON( String trip, String status ) {}

	private record SpotJSON( String slug, String name, String category, double lat, double lon, String description, String url, String status, String image, String trip, List<VisitJSON> visits ) {}

	private record RouteJSON( String slug, String name, String color, int distanceKm, int durationMin, String description, List<String> spots, String trip, String status ) {}

	private record EventJSON( String title, String start, String end, String description, String spot, String route, String person ) {}

	private record TripJSON( String slug, String name, String status, String start, String end, String description, List<EventJSON> events ) {}

	public static void load() {
		final ObjectContext oc = VacationCore.newContext();

		if( !ObjectSelect.query( Spot.class ).select( oc ).isEmpty() ) {
			logger.info( "DB already contains spots — skipping seed" );
			return;
		}

		final Map<String, Spot> spotsBySlug = new HashMap<>();
		final Map<String, DrivingRoute> routesBySlug = new HashMap<>();
		final Map<String, Trip> tripsBySlug = new HashMap<>();

		for( final TripJSON json : SeedData.<TripJSON> parse( "trips.json", new TypeToken<List<TripJSON>>() {}.getType() ) ) {
			final Trip trip = oc.newObject( Trip.class );
			trip.setSlug( json.slug() );
			trip.setName( json.name() );
			trip.setStatus( json.status() );
			trip.setStart( LocalDate.parse( json.start() ) );
			trip.setEnd( LocalDate.parse( json.end() ) );
			trip.setDescription( json.description() );
			tripsBySlug.put( json.slug(), trip );
		}

		for( final SpotJSON json : SeedData.<SpotJSON> parse( "spots.json", new TypeToken<List<SpotJSON>>() {}.getType() ) ) {
			final Spot spot = oc.newObject( Spot.class );
			spot.setSlug( json.slug() );
			spot.setName( json.name() );
			spot.setCategory( json.category() );
			spot.setLat( json.lat() );
			spot.setLon( json.lon() );
			spot.setDescription( json.description() );
			spot.setUrl( json.url() );
			spot.setImage( json.image() );
			spotsBySlug.put( json.slug(), spot );

			// The JSON entry's trip/status describe our plan to go there — that's a Visit, not part of the location
			if( json.trip() != null || json.status() != null ) {
				final Visit visit = oc.newObject( Visit.class );
				visit.setSpot( spot );
				visit.setTrip( tripsBySlug.get( json.trip() ) );
				visit.setStatus( json.status() );
			}

			// A spot visited on several trips lists additional visits explicitly
			for( final VisitJSON visitJSON : json.visits() == null ? List.<VisitJSON> of() : json.visits() ) {
				final Visit visit = oc.newObject( Visit.class );
				visit.setSpot( spot );
				visit.setTrip( tripsBySlug.get( visitJSON.trip() ) );
				visit.setStatus( visitJSON.status() );
			}
		}

		for( final RouteJSON json : SeedData.<RouteJSON> parse( "routes.json", new TypeToken<List<RouteJSON>>() {}.getType() ) ) {
			final DrivingRoute route = oc.newObject( DrivingRoute.class );
			route.setSlug( json.slug() );
			route.setName( json.name() );
			route.setColor( json.color() );
			route.setDistanceKm( json.distanceKm() );
			route.setDurationMin( json.durationMin() );
			route.setDescription( json.description() );
			route.setStatus( json.status() );
			route.setTrip( tripsBySlug.get( json.trip() ) );
			routesBySlug.put( json.slug(), route );

			int sortOrder = 0;

			for( final String spotSlug : json.spots() == null ? List.<String> of() : json.spots() ) {
				final Spot spot = spotsBySlug.get( spotSlug );

				if( spot == null ) {
					logger.warn( "Route '{}' references unknown spot '{}' — skipped", json.slug(), spotSlug );
					continue;
				}

				final RouteStop stop = oc.newObject( RouteStop.class );
				stop.setRoute( route );
				stop.setSpot( spot );
				stop.setSortOrder( sortOrder++ );
			}
		}

		// Trip events come nested in trips.json; standalone events (belonging to no trip) from events.json
		for( final TripJSON json : SeedData.<TripJSON> parse( "trips.json", new TypeToken<List<TripJSON>>() {}.getType() ) ) {
			for( final EventJSON eventJSON : json.events() == null ? List.<EventJSON> of() : json.events() ) {
				createEvent( oc, eventJSON, tripsBySlug.get( json.slug() ), spotsBySlug, routesBySlug );
			}
		}

		for( final EventJSON json : SeedData.<EventJSON> parse( "events.json", new TypeToken<List<EventJSON>>() {}.getType() ) ) {
			createEvent( oc, json, null, spotsBySlug, routesBySlug );
		}

		oc.commitChanges();
		logger.info( "Seeded DB from JSON datasets" );
	}

	private static void createEvent( final ObjectContext oc, final EventJSON json, final Trip trip, final Map<String, Spot> spotsBySlug, final Map<String, DrivingRoute> routesBySlug ) {
		final CalendarEvent event = oc.newObject( CalendarEvent.class );
		event.setTrip( trip );
		event.setTitle( json.title() );
		event.setStart( LocalDate.parse( json.start() ) );
		event.setEnd( LocalDate.parse( json.end() != null ? json.end() : json.start() ) );
		event.setDescription( json.description() );
		event.setPerson( json.person() );
		event.setSpot( json.spot() != null ? spotsBySlug.get( json.spot() ) : null );
		event.setRoute( json.route() != null ? routesBySlug.get( json.route() ) : null );
	}

	private static <T> List<T> parse( final String resourceName, final Type type ) {
		final String jsonString = ERXUtilities.readStringFromBundleResource( resourceName, null, null, StandardCharsets.UTF_8 );
		return new GsonBuilder().create().fromJson( jsonString, type );
	}
}
