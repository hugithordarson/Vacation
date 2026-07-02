package vacation;

import java.util.List;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.Trip;

public class Trips {

	public static List<Trip> all() {
		return ObjectSelect.query( Trip.class )
				.orderBy( Trip.START.asc() )
				.select( VacationCore.sharedContext() );
	}

	public static Trip bySlug( final String slug ) {
		return ObjectSelect.query( Trip.class )
				.where( Trip.SLUG.eq( slug ) )
				.selectFirst( VacationCore.sharedContext() );
	}

	/**
	 * @return The trip currently being planned — the first "planning" trip, falling back to the earliest trip
	 */
	public static Trip current() {
		return all()
				.stream()
				.filter( Trip::planning )
				.findFirst()
				.orElse( all().isEmpty() ? null : all().getFirst() );
	}
}
