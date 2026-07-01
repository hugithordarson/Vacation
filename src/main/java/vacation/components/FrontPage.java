package vacation.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.DrivingRoute;
import vacation.Routes;

public class FrontPage extends VacationComponent {

	public DrivingRoute currentRoute;

	public FrontPage( WOContext context ) {
		super( context );
	}

	public List<DrivingRoute> routes() {
		return Routes.all();
	}

	public String currentRouteLink() {
		return "/route/" + currentRoute.slug();
	}

	public String currentRouteDotStyle() {
		return "color: " + currentRoute.color();
	}
}
