package vacation.data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import vacation.data.auto._Spot;

/**
 * A location — a pure geographic fact we're keeping track of. Whether/when we go there
 * is a Visit; a Spot can exist without any visit planned.
 */

public class Spot extends _Spot {

	private static final long serialVersionUID = 1L;

	/**
	 * @return The spot's first visit — for now spots have at most one; this becomes visit-per-trip aware later
	 */
	public Visit firstVisit() {
		return visits().isEmpty() ? null : visits().getFirst();
	}

	/**
	 * @return The planning status of this spot's visit (null when no visit or undecided)
	 */
	public String visitStatus() {
		return firstVisit() == null ? null : firstVisit().status();
	}

	public boolean maybe() {
		return "kannski".equals( visitStatus() );
	}

	public boolean suggestion() {
		return "tillaga".equals( visitStatus() );
	}

	public String googleMapsURL() {
		return "https://www.google.com/maps?q=%s,%s".formatted( lat(), lon() );
	}

	public String appleMapsURL() {
		return "https://maps.apple.com/?ll=%s,%s&q=%s".formatted( lat(), lon(), URLEncoder.encode( name(), StandardCharsets.UTF_8 ) );
	}

	public String openStreetMapURL() {
		return "https://www.openstreetmap.org/?mlat=%s&mlon=%s#map=14/%s/%s".formatted( lat(), lon(), lat(), lon() );
	}
}
