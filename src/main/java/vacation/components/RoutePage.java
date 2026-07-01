package vacation.components;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.DrivingRoute;
import vacation.Spot;

public class RoutePage extends VacationComponent {

	public DrivingRoute route;
	public Spot currentStop;

	public RoutePage( WOContext context ) {
		super( context );
	}

	public List<Spot> stops() {
		return route.stops();
	}

	public boolean hasStops() {
		return !stops().isEmpty();
	}

	public String currentStopLink() {
		return "/spot/" + currentStop.slug();
	}

	public String stopsJSON() {
		return new GsonBuilder().create().toJson( stops() );
	}
}
