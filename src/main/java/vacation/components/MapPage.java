package vacation.components;

import app.VacationComponent;
import ng.appserver.NGContext;
import vacation.data.Trip;

public class MapPage extends VacationComponent {

	/**
	 * The trip whose spots/routes the map shows — null shows everything. Set by the route handler.
	 */
	public Trip trip;

	public MapPage( NGContext context ) {
		super( context );
	}

	public String heading() {
		return trip == null ? "Allir staðirnir" : trip.name();
	}
}
