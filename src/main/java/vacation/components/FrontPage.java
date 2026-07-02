package vacation.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Routes;
import vacation.Spots;
import vacation.data.DrivingRoute;
import vacation.data.Spot;

public class FrontPage extends VacationComponent {

	public DrivingRoute currentRoute;

	public FrontPage( WOContext context ) {
		super( context );
	}

	public List<DrivingRoute> routes() {
		return Routes.all();
	}

	public Spot gisting() {
		return Spots.bySlug( "gisting" );
	}

	public boolean hasHouseImage() {
		return gisting() != null && gisting().image() != null;
	}

	public String currentRouteLink() {
		return "/route/" + currentRoute.slug();
	}

	public String currentRouteDotStyle() {
		return "color: " + currentRoute.color();
	}
}
