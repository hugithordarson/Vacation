package vacation.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import vacation.data.auto._Trip;

/**
 * A vacation — first being planned ("planning"), eventually enriched with photos and
 * journal entries once it's over ("done"). "idea" marks trips that are just a twinkle in the eye.
 */

public class Trip extends _Trip {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern( "d. MMMM", Locale.of( "is" ) );
	private static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern( "d. MMMM yyyy", Locale.of( "is" ) );

	public List<LocalDate> days() {
		return start().datesUntil( end().plusDays( 1 ) ).toList();
	}

	/**
	 * @return The public iCloud page of the trip's shared photo album, if one is linked
	 */
	public String sharedAlbumURL() {
		return sharedAlbumToken() == null ? null : "https://www.icloud.com/sharedalbum/#" + sharedAlbumToken();
	}

	public boolean planning() {
		return "planning".equals( status() );
	}

	public String statusLabel() {
		return switch( status() == null ? "" : status() ) {
			case "planning" -> "í skipulagningu";
			case "idea" -> "hugmynd";
			case "done" -> "lokið";
			default -> "";
		};
	}

	/**
	 * @return E.g. "12.–19. júlí 2026", or "28. júní – 5. júlí 2027" when the span crosses months
	 */
	public String spanLabel() {

		if( start().getMonth() == end().getMonth() && start().getYear() == end().getYear() ) {
			return start().getDayOfMonth() + ".–" + DAY_MONTH_YEAR.format( end() );
		}

		return DAY_MONTH.format( start() ) + " – " + DAY_MONTH_YEAR.format( end() );
	}
}
