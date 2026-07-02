package vacation.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import vacation.data.auto._TripEvent;

/**
 * Something happening during the trip — either on a single day or spanning a period.
 */

public class TripEvent extends _TripEvent {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern( "d. MMMM", Locale.of( "is" ) );

	public boolean period() {
		return !start().equals( end() );
	}

	public boolean occursOn( final LocalDate day ) {
		return !day.isBefore( start() ) && !day.isAfter( end() );
	}

	public String spanLabel() {
		return start().getDayOfMonth() + ".–" + DAY_FORMATTER.format( end() );
	}
}
