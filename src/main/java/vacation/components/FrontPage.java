package vacation.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Routes;
import vacation.Spots;
import vacation.Trips;
import vacation.data.DrivingRoute;
import vacation.data.Spot;
import vacation.data.Trip;

public class FrontPage extends VacationComponent {

	public DrivingRoute currentRoute;
	public Trip currentTrip;

	public FrontPage( WOContext context ) {
		super( context );
	}

	public Trip trip() {
		return Trips.current();
	}

	public List<Trip> trips() {
		return Trips.all();
	}

	public String currentTripLink() {
		return "/calendar/" + currentTrip.slug();
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
