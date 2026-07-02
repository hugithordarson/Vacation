package vacation;

import java.time.LocalDate;
import java.util.List;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.TripEvent;

public class Events {

	public static List<TripEvent> all() {
		return ObjectSelect.query( TripEvent.class )
				.select( VacationCore.sharedContext() );
	}

	public static List<TripEvent> on( final LocalDate day ) {
		return all()
				.stream()
				.filter( event -> event.occursOn( day ) )
				.toList();
	}
}
