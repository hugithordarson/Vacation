package vacation.components;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.data.Trip;

public class MapPage extends VacationComponent {

	/**
	 * The trip whose spots/routes the map shows — null shows everything. Set by the route handler.
	 */
	public Trip trip;

	public MapPage( WOContext context ) {
		super( context );
	}

	public String heading() {
		return trip == null ? "Allir staðirnir" : trip.name();
	}
}
