package app;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.routes.RouteInvocation;
import er.extensions.routes.RouteTable;
import vacation.DrivingRoute;
import vacation.Routes;
import vacation.Spot;
import vacation.Spots;
import vacation.components.CalendarPage;
import vacation.components.FrontPage;
import vacation.components.MapPage;
import vacation.components.RoutePage;
import vacation.components.SpotPage;

public class Application extends ERXApplication {

	public Application() {
		setupRoutes();
	}

	private void setupRoutes() {
		RouteTable.defaultRouteTable().map( "/", FrontPage.class );
		RouteTable.defaultRouteTable().map( "/map/", MapPage.class );
		RouteTable.defaultRouteTable().map( "/calendar/", CalendarPage.class );
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
