package app;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOResponse;

import er.extensions.appserver.ERXApplication;
import er.extensions.routes.RouteInvocation;
import er.extensions.routes.RouteTable;
import vacation.Spot;
import vacation.Spots;
import vacation.components.CalendarPage;
import vacation.components.FrontPage;
import vacation.components.MapPage;
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
	}

	private WOActionResults spotPage( RouteInvocation invocation ) {
		String slug = invocation.url().substring( "/spot/".length() );

		if( slug.endsWith( "/" ) ) {
			slug = slug.substring( 0, slug.length() - 1 );
		}

		final Spot spot = Spots.bySlug( slug );

		if( spot == null ) {
			final WOResponse response = new WOResponse();
			response.setStatus( 404 );
			response.setContent( "Enginn staður með slóðina: " + slug );
			return response;
		}

		final SpotPage page = pageWithName( SpotPage.class, invocation.context() );
		page.spot = spot;
		return page;
	}
}
