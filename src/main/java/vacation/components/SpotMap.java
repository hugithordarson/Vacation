package vacation.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Routes;
import vacation.Spots;
import vacation.data.DrivingRoute;
import vacation.data.Spot;

/**
 * A Leaflet map showing all spots and driving routes. Expects Leaflet's CSS/JS to be included by the containing page.
 * Bindings: height (CSS height for the map, defaults to 600px)
 */

public class SpotMap extends VacationComponent {

	public SpotMap( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public String spotsJSON() {
		final List<Map<String, Object>> result = new ArrayList<>();

		for( final Spot spot : Spots.all() ) {
			final Map<String, Object> map = new LinkedHashMap<>();
			map.put( "slug", spot.slug() );
			map.put( "name", spot.name() );
			map.put( "category", spot.category() );
			map.put( "lat", spot.lat() );
			map.put( "lon", spot.lon() );
			map.put( "status", spot.status() );

			if( spot.image() != null ) {
				map.put( "imageURL", application().resourceManager().urlForResourceNamed( spot.image(), null, null, context().request() ) );
			}

			result.add( map );
		}

		return new GsonBuilder().create().toJson( result );
	}

	public String routesJSON() {
		final List<Map<String, Object>> result = new ArrayList<>();

		for( final DrivingRoute route : Routes.all() ) {
			final Map<String, Object> map = new LinkedHashMap<>();
			map.put( "slug", route.slug() );
			map.put( "name", route.name() );
			map.put( "color", route.color() );
			result.add( map );
		}

		return new GsonBuilder().create().toJson( result );
	}

	public String style() {
		return "height: " + stringValueForBinding( "height", "600px" );
	}
}
