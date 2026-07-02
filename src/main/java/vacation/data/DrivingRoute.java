package vacation.data;

import java.util.Comparator;
import java.util.List;

import vacation.data.auto._DrivingRoute;

/**
 * A drive we might take. The road-following geometry (fetched once from OSRM)
 * lives in the routes/[slug].geojson resource, not the DB.
 */

public class DrivingRoute extends _DrivingRoute {

	private static final long serialVersionUID = 1L;

	public String durationLabel() {
		return "%d:%02d klst".formatted( durationMin() / 60, durationMin() % 60 );
	}

	/**
	 * @return The spots along this route, in driving order
	 */
	public List<Spot> stops() {
		return routeStops()
				.stream()
				.sorted( Comparator.comparingInt( RouteStop::sortOrder ) )
				.map( RouteStop::spot )
				.toList();
	}
}
