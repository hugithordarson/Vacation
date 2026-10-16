package app;

import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import ng.appserver.NGActionResults;
import ng.appserver.NGApplication;
import ng.appserver.http.NGRequest;
import ng.appserver.http.NGResponse;
import ng.appserver.http.NGResponses;
import ng.appserver.privates.NGParsedURI;
import ng.plugins.Elements;
import ng.plugins.Routes;
import vacation.DrivingRoutes;
import vacation.SeedData;
import vacation.SharedAlbum;
import vacation.Spots;
import vacation.Trips;
import vacation.components.CalendarPage;
import vacation.components.FrontPage;
import vacation.components.MapPage;
import vacation.components.PhotosPage;
import vacation.components.RoutePage;
import vacation.components.SpotPage;
import vacation.components.TripPage;
import vacation.data.DrivingRoute;
import vacation.data.Spot;
import vacation.data.Trip;

public class Application extends NGApplication {

	public static void main( String[] args ) {
		NGApplication.run( args, Application.class );
	}

	public Application() {
		VacationCore.runtime();
	}

	/**
	 * Seeding reads the JSON datasets from app-resources, which aren't reachable until the application has been initialized — so it happens here, not in the constructor
	 */
	@Override
	public void load( NGApplication application ) {
		SeedData.load();
		SharedAlbum.warmUp( Trips.all().stream().map( Trip::sharedAlbumToken ).toList() );
	}

	@Override
	public Elements elements() {
		return super
				.elements()
				.elementPackage( "vacation.components" );
	}

	@Override
	public Routes routes() {
		return Routes
				.create()
				.map( "/", FrontPage.class )
				.map( "/trip/*", this::tripPage )
				.map( "/map/*", this::mapPage )
				.map( "/calendar/*", this::calendarPage )
				.map( "/photos/*", this::photosPage )
				.map( "/spot/*", this::spotPage )
				.map( "/drive/*", this::routePage )
				.map( "/drive-geo/*", this::routeGeometry );
	}

	/**
	 * @return The contents of the named app resource (from ng/app/app-resources) as a string, or null if it doesn't exist
	 */
	public static String appResourceString( final String resourcePath ) {
		return application()
				.resourceManager()
				.obtainAppResource( "app", resourcePath )
				.map( resource -> new String( resource.bytes(), StandardCharsets.UTF_8 ) )
				.orElse( null );
	}

	private NGActionResults routePage( NGRequest request ) {
		final DrivingRoute route = DrivingRoutes.bySlug( slug( request ) );

		if( route == null ) {
			return notFound( "Engin leið með þessari slóð" );
		}

		final RoutePage page = pageWithName( RoutePage.class, request.context() );
		page.route = route;
		return page;
	}

	private NGActionResults routeGeometry( NGRequest request ) {
		final String geometryJSON = DrivingRoutes.geometryJSON( slug( request ) );

		if( geometryJSON == null ) {
			return notFound( "Engin leið með þessari slóð" );
		}

		final NGResponse response = NGResponses.ok( geometryJSON );
		response.setHeader( "content-type", "application/geo+json" );
		return response;
	}

	/**
	 * @return The second path component of the request's URL (e.g. "budardalur-2026" for /trip/budardalur-2026/) — empty when there's none
	 */
	private static String slug( final NGRequest request ) {
		return NGParsedURI.of( request.uri() ).getString( 1, "" );
	}

	private static NGResponse notFound( final String message ) {
		return NGResponses.of( 404, message );
	}

	private NGActionResults tripPage( NGRequest request ) {
		final Trip trip = Trips.bySlug( slug( request ) );

		if( trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final TripPage page = pageWithName( TripPage.class, request.context() );
		page.trip = trip;
		return page;
	}

	private NGActionResults mapPage( NGRequest request ) {
		final String slug = slug( request );
		final Trip trip = slug.isEmpty() ? null : Trips.bySlug( slug );

		if( !slug.isEmpty() && trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final MapPage page = pageWithName( MapPage.class, request.context() );
		page.trip = trip;
		return page;
	}

	private NGActionResults photosPage( NGRequest request ) {
		final String slug = slug( request );
		final Trip trip = slug.isEmpty() ? null : Trips.bySlug( slug );

		if( !slug.isEmpty() && trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final PhotosPage page = pageWithName( PhotosPage.class, request.context() );
		page.trip = trip;
		return page;
	}

	private NGActionResults calendarPage( NGRequest request ) {
		final String slug = slug( request );

		// No slug shows the whole plan (all trips); a slug shows that trip's calendar
		Trip trip = null;

		if( !slug.isEmpty() ) {
			trip = Trips.bySlug( slug );

			if( trip == null ) {
				return notFound( "Engin ferð með þessari slóð" );
			}
		}

		YearMonth month = trip != null ? YearMonth.from( trip.start() ) : YearMonth.now();

		// formValueForKey() throws if the parameter is repeated, so we take the first value ourselves
		final String monthParam = request.formValuesForKey( "month" ).stream().findFirst().orElse( null );

		if( monthParam != null ) {
			try {
				month = YearMonth.parse( monthParam );
			}
			catch( DateTimeParseException e ) {
				// Malformed month parameter — keep the default
			}
		}

		final CalendarPage page = pageWithName( CalendarPage.class, request.context() );
		page.trip = trip;
		page.month = month;
		return page;
	}

	private NGActionResults spotPage( NGRequest request ) {
		final Spot spot = Spots.bySlug( slug( request ) );

		if( spot == null ) {
			return notFound( "Enginn staður með þessari slóð" );
		}

		final SpotPage page = pageWithName( SpotPage.class, request.context() );
		page.spot = spot;
		return page;
	}
}
