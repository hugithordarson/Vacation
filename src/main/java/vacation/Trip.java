package vacation;

import java.time.LocalDate;
import java.util.List;

/**
 * A vacation. Hardcoded for now — the plan is for this to become a DB entity, making the app track many trips.
 */

public record Trip( String name, LocalDate start, LocalDate end ) {

	public static final Trip CURRENT = new Trip( "Búðardalur 2026", LocalDate.of( 2026, 7, 12 ), LocalDate.of( 2026, 7, 19 ) );

	public List<LocalDate> days() {
		return start.datesUntil( end.plusDays( 1 ) ).toList();
	}
}
