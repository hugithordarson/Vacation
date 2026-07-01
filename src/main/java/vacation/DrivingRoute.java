package vacation;

import java.util.List;
import java.util.Objects;

/**
 * A drive we might take. Metadata lives in routes.json; the road-following geometry
 * (fetched once from OSRM, cached as a resource) in routes/[slug].geojson.
 *
 * @param spots Slugs of the spots along the way, in driving order
 */

public record DrivingRoute( String slug, String name, String color, int distanceKm, int durationMin, String description, List<String> spots ) {

	public String durationLabel() {
		return "%d:%02d klst".formatted( durationMin / 60, durationMin % 60 );
	}

	/**
	 * @return The spots along this route, in driving order
	 */
	public List<Spot> stops() {

		if( spots == null ) {
			return List.of();
		}

		return spots
				.stream()
				.map( Spots::bySlug )
				.filter( Objects::nonNull )
				.toList();
	}
}
