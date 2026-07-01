package vacation.components;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.DrivingRoute;

public class RoutePage extends VacationComponent {

	public DrivingRoute route;

	public RoutePage( WOContext context ) {
		super( context );
	}
}
