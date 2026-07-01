package vacation.components;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Spot;

public class SpotPage extends VacationComponent {

	public Spot spot;

	public SpotPage( WOContext context ) {
		super( context );
	}
}
