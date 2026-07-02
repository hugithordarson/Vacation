package vacation.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Events;
import vacation.Trips;
import vacation.data.Trip;
import vacation.data.TripEvent;

/**
 * Week-grid calendar for the trip: one column per day, period events as bars spanning
 * their days, single-day events as cards in their day's cell.
 */

public class CalendarPage extends VacationComponent {

	private static final DateTimeFormatter DAY_HEADER_FORMATTER = DateTimeFormatter.ofPattern( "EEE d.M.", Locale.of( "is" ) );

	public LocalDate currentDay;
	public TripEvent currentEvent;

	public CalendarPage( WOContext context ) {
		super( context );
	}

	public Trip trip() {
		return Trips.current();
	}

	public List<LocalDate> days() {
		return trip().days();
	}

	public String currentDayHeaderLabel() {
		final String label = DAY_HEADER_FORMATTER.format( currentDay );
		return label.substring( 0, 1 ).toUpperCase() + label.substring( 1 );
	}

	/**
	 * @return The 1-based grid column for the given day, clamped to the trip's span
	 */
	private int columnForDay( final LocalDate day ) {
		final long index = ChronoUnit.DAYS.between( trip().start(), day ) + 1;
		return (int)Math.clamp( index, 1, days().size() );
	}

	public List<TripEvent> periodEvents() {
		return Events.all()
				.stream()
				.filter( TripEvent::period )
				.toList();
	}

	public String currentPeriodEventStyle() {
		return "grid-column: %d / %d".formatted( columnForDay( currentEvent.start() ), columnForDay( currentEvent.end() ) + 1 );
	}

	public String currentDayCellStyle() {
		return "grid-column: " + columnForDay( currentDay );
	}

	public List<TripEvent> currentDaySingleEvents() {
		return Events.on( currentDay )
				.stream()
				.filter( event -> !event.period() )
				.toList();
	}
}
