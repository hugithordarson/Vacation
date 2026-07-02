package vacation.data;

import java.time.LocalDate;
import java.util.List;

import vacation.data.auto._Trip;

public class Trip extends _Trip {

	private static final long serialVersionUID = 1L;

	public List<LocalDate> days() {
		return start().datesUntil( end().plusDays( 1 ) ).toList();
	}
}
