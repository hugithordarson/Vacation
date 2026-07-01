package vacation.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Events;
import vacation.Trip;
import vacation.TripEvent;

public class CalendarPage extends VacationComponent {

	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern( "EEEE d. MMMM", Locale.of( "is" ) );

	public LocalDate currentDay;
	public TripEvent currentEvent;

	public CalendarPage( WOContext context ) {
		super( context );
	}

	public Trip trip() {
		return Trip.CURRENT;
	}

	public List<LocalDate> days() {
		return trip().days();
	}

	public String currentDayLabel() {
		final String label = DAY_FORMATTER.format( currentDay );
		return label.substring( 0, 1 ).toUpperCase() + label.substring( 1 );
	}

	public List<TripEvent> currentDayEvents() {
		return Events.on( currentDay );
	}

	public boolean currentDayIsEmpty() {
		return currentDayEvents().isEmpty();
	}
}
