package vacation;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.Trip;

public class Trips {

	/**
	 * @return The trip currently being planned. For now that's simply the only trip — this becomes smarter once there are many.
	 */
	public static Trip current() {
		return ObjectSelect.query( Trip.class )
				.selectFirst( VacationCore.sharedContext() );
	}
}
