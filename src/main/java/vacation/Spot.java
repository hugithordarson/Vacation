package vacation;

/**
 * A place we might want to visit. Loaded from spots.json — will eventually become a DB entity.
 *
 * @param url Optional link to an external page about the spot (booking, official site etc.)
 * @param status Decidedness: null/absent = part of the plan, "kannski" = maybe, "tillaga" = an unsorted suggestion
 * @param image Optional image path under webserver-resources, e.g. "spots/dynjandi.jpg"
 */

public record Spot( String slug, String name, String category, double lat, double lon, String description, String url, String status, String image ) {

	public boolean maybe() {
		return "kannski".equals( status );
	}

	public boolean suggestion() {
		return "tillaga".equals( status );
	}
}
