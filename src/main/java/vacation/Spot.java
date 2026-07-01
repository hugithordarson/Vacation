package vacation;

/**
 * A place we might want to visit. Loaded from spots.json — will eventually become a DB entity.
 *
 * @param url Optional link to an external page about the spot (booking, official site etc.)
 */

public record Spot( String slug, String name, String category, double lat, double lon, String description, String url ) {}
