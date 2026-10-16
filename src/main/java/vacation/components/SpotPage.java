package vacation.components;

import app.VacationComponent;
import ng.appserver.NGContext;
import vacation.data.Spot;

public class SpotPage extends VacationComponent {

	public Spot spot;

	public SpotPage( NGContext context ) {
		super( context );
	}
}
