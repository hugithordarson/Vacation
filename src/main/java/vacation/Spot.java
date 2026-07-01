package vacation;

/**
 * A place we might want to visit. Loaded from spots.json — will eventually become a DB entity.
 *
 * @param url Optional link to an external page about the spot (booking, official site etc.)
 * @param status "kannski" marks a spot we haven't decided on yet; null/absent means it's part of the plan
 */

public record Spot( String slug, String name, String category, double lat, double lon, String description, String url, String status ) {

	public boolean maybe() {
		return "kannski".equals( status );
	}
}
