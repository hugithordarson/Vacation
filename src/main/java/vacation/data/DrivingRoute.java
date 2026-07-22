package vacation.data;

import java.util.Comparator;
import java.util.List;

import vacation.data.auto._DrivingRoute;

/**
 * A drive. The road-following geometry (fetched once from OSRM) lives in the
 * routes/[slug].geojson resource, not the DB.
 *
 * status: null = part of the plan, "tillaga" = suggested, "kannski" = maybe, "ekin" = actually driven
 */

public class DrivingRoute extends _DrivingRoute {

	private static final long serialVersionUID = 1L;

	public String durationLabel() {
		return "%d:%02d klst".formatted( durationMin() / 60, durationMin() % 60 );
	}

	public boolean ekin() {
		return "ekin".equals( status() );
	}

	/**
	 * @return True for the undecided statuses — drawn dashed on maps
	 */
	public boolean dashed() {
		return "tillaga".equals( status() ) || "kannski".equals( status() );
	}

	public String statusLabel() {
		return switch( status() == null ? "" : status() ) {
			case "tillaga" -> "tillaga";
			case "kannski" -> "kannski?";
			case "ekin" -> "ekin";
			default -> null;
		};
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
