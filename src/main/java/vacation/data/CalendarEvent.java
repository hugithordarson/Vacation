package vacation.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import vacation.data.auto._CalendarEvent;

/**
 * Something scheduled — on a single day or spanning a period. Optionally belongs to a trip,
 * happens at a spot, follows a route, or concerns a family member (person, plain text for now).
 * The calendar sources these from whatever context it's viewed in: a trip, the family as a whole…
 */

public class CalendarEvent extends _CalendarEvent {

	private static final long serialVersionUID = 1L;

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern( "d. MMMM", Locale.of( "is" ) );
	private static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern( "d. MMMM yyyy", Locale.of( "is" ) );

	public boolean period() {
		return !start().equals( end() );
	}

	public boolean occursOn( final LocalDate day ) {
		return !day.isBefore( start() ) && !day.isAfter( end() );
	}

	public String spanLabel() {
		return start().getDayOfMonth() + ".–" + DAY_FORMATTER.format( end() );
	}

	public String dateLabel() {
		return period() ? spanLabel() : DAY_MONTH_YEAR.format( start() );
	}
}
