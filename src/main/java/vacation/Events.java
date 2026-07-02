package vacation;

import java.time.LocalDate;
import java.util.List;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.CalendarEvent;

public class Events {

	public static List<CalendarEvent> all() {
		return ObjectSelect.query( CalendarEvent.class )
				.select( VacationCore.sharedContext() );
	}

	public static List<CalendarEvent> on( final LocalDate day ) {
		return all()
				.stream()
				.filter( event -> event.occursOn( day ) )
				.toList();
	}
}
