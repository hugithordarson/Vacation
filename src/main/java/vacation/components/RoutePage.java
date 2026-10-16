package vacation.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.GsonBuilder;

import app.VacationComponent;
import ng.appserver.NGContext;
import vacation.data.DrivingRoute;
import vacation.data.Spot;

public class RoutePage extends VacationComponent {

	public DrivingRoute route;
	public Spot currentStop;

	public RoutePage( NGContext context ) {
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

	public String dashArrayJS() {
		return route.dashed() ? "'8 8'" : "null";
	}

	public String stopsJSON() {
		final List<Map<String, Object>> result = new ArrayList<>();

		for( final Spot spot : stops() ) {
			final Map<String, Object> map = new LinkedHashMap<>();
			map.put( "slug", spot.slug() );
			map.put( "name", spot.name() );
			map.put( "category", spot.category() );
			map.put( "lat", spot.lat() );
			map.put( "lon", spot.lon() );
			map.put( "status", spot.visitStatus() );
			result.add( map );
		}

		return new GsonBuilder().create().toJson( result );
	}
}
