package vacation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Something happening during the trip — either on a single day or spanning a period.
 */

public record TripEvent( String title, LocalDate start, LocalDate end, String description ) {

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern( "d. MMMM", Locale.of( "is" ) );

	public static TripEvent on( final String title, final LocalDate day, final String description ) {
		return new TripEvent( title, day, day, description );
	}

	public static TripEvent span( final String title, final LocalDate start, final LocalDate end, final String description ) {
		return new TripEvent( title, start, end, description );
	}

	public boolean period() {
		return !start.equals( end );
	}

	public boolean occursOn( final LocalDate day ) {
		return !day.isBefore( start ) && !day.isAfter( end );
	}

	public String spanLabel() {
		return start.getDayOfMonth() + ".–" + DAY_FORMATTER.format( end );
	}
}
