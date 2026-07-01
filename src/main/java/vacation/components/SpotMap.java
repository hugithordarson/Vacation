package vacation.components;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Spots;

/**
 * A Leaflet map showing all spots. Expects Leaflet's CSS/JS to be included by the containing page.
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
		return Spots.asJSON();
	}

	public String style() {
		return "height: " + stringValueForBinding( "height", "600px" );
	}
}
