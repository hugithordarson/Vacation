package vacation;

/**
 * A drive we might take. Metadata lives in routes.json; the road-following geometry
 * (fetched once from OSRM, cached as a resource) in routes/[slug].geojson.
 */

public record DrivingRoute( String slug, String name, String color, int distanceKm, int durationMin, String description ) {

	public String durationLabel() {
		return "%d:%02d klst".formatted( durationMin / 60, durationMin % 60 );
	}
}
