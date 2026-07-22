package app;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.routes.RouteInvocation;
import er.extensions.routes.RouteTable;
import vacation.Routes;
import vacation.SeedData;
import vacation.Spots;
import vacation.Trips;
import vacation.data.DrivingRoute;
import vacation.data.Spot;
import vacation.data.Trip;
import vacation.components.CalendarPage;
import vacation.components.FrontPage;
import vacation.components.MapPage;
import vacation.components.PhotosPage;
import vacation.components.RoutePage;
import vacation.components.SpotPage;
import vacation.components.TripPage;

public class Application extends ERXApplication {

	public Application() {
		VacationCore.runtime();
		SeedData.load();
		setupRoutes();
	}

	private void setupRoutes() {
		RouteTable.defaultRouteTable().map( "/", FrontPage.class );
		RouteTable.defaultRouteTable().map( "/trip/*", this::tripPage );
		RouteTable.defaultRouteTable().map( "/map/*", this::mapPage );
		RouteTable.defaultRouteTable().map( "/calendar/*", this::calendarPage );
		RouteTable.defaultRouteTable().map( "/photos/*", this::photosPage );
		RouteTable.defaultRouteTable().map( "/spot/*", this::spotPage );
		RouteTable.defaultRouteTable().map( "/route/*", this::routePage );
		RouteTable.defaultRouteTable().map( "/route-geo/*", this::routeGeometry );
	}

	private WOActionResults routePage( RouteInvocation invocation ) {
		final DrivingRoute route = Routes.bySlug( lastPathComponent( invocation, "/route/" ) );

		if( route == null ) {
			return notFound( "Engin leið með þessari slóð" );
		}

		final RoutePage page = pageWithName( RoutePage.class, invocation.context() );
		page.route = route;
		return page;
	}

	private WOActionResults routeGeometry( RouteInvocation invocation ) {
		final String geometryJSON = Routes.geometryJSON( lastPathComponent( invocation, "/route-geo/" ) );

		if( geometryJSON == null ) {
			return notFound( "Engin leið með þessari slóð" );
		}

		final WOResponse response = new WOResponse();
		response.setHeader( "application/geo+json", "content-type" );
		response.setContent( geometryJSON );
		return response;
	}

	private static String lastPathComponent( final RouteInvocation invocation, final String prefix ) {
		String result = invocation.url().substring( prefix.length() );

		if( result.endsWith( "/" ) ) {
			result = result.substring( 0, result.length() - 1 );
		}

		return result;
	}

	private static WOResponse notFound( final String message ) {
		final WOResponse response = new WOResponse();
		response.setStatus( 404 );
		response.setContent( message );
		return response;
	}

	private WOActionResults tripPage( RouteInvocation invocation ) {
		final Trip trip = Trips.bySlug( lastPathComponent( invocation, "/trip/" ) );

		if( trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final TripPage page = pageWithName( TripPage.class, invocation.context() );
		page.trip = trip;
		return page;
	}

	private WOActionResults mapPage( RouteInvocation invocation ) {
		final String slug = lastPathComponent( invocation, "/map/" );
		final Trip trip = slug.isEmpty() ? null : Trips.bySlug( slug );

		if( !slug.isEmpty() && trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final MapPage page = pageWithName( MapPage.class, invocation.context() );
		page.trip = trip;
		return page;
	}

	private WOActionResults photosPage( RouteInvocation invocation ) {
		final String slug = lastPathComponent( invocation, "/photos/" );
		final Trip trip = slug.isEmpty() ? null : Trips.bySlug( slug );

		if( !slug.isEmpty() && trip == null ) {
			return notFound( "Engin ferð með þessari slóð" );
		}

		final PhotosPage page = pageWithName( PhotosPage.class, invocation.context() );
		page.trip = trip;
		return page;
	}

	private WOActionResults calendarPage( RouteInvocation invocation ) {
		final String slug = lastPathComponent( invocation, "/calendar/" );

		// No slug shows the whole plan (all trips); a slug shows that trip's calendar
		Trip trip = null;

		if( !slug.isEmpty() ) {
			trip = Trips.bySlug( slug );

			if( trip == null ) {
				return notFound( "Engin ferð með þessari slóð" );
			}
		}

		YearMonth month = trip != null ? YearMonth.from( trip.start() ) : YearMonth.now();

		final String monthParam = invocation.request().stringFormValueForKey( "month" );

		if( monthParam != null ) {
			try {
				month = YearMonth.parse( monthParam );
			}
			catch( DateTimeParseException e ) {
				// Malformed month parameter — keep the default
			}
		}

		final CalendarPage page = pageWithName( CalendarPage.class, invocation.context() );
		page.trip = trip;
		page.month = month;
		return page;
	}

	private WOActionResults spotPage( RouteInvocation invocation ) {
		final Spot spot = Spots.bySlug( lastPathComponent( invocation, "/spot/" ) );

		if( spot == null ) {
			return notFound( "Enginn staður með þessari slóð" );
		}

		final SpotPage page = pageWithName( SpotPage.class, invocation.context() );
		page.spot = spot;
		return page;
	}
}
