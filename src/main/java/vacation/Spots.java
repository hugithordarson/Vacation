package vacation;

import java.util.List;
import java.util.Objects;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.Spot;
import vacation.data.Trip;
import vacation.data.Visit;

public class Spots {

	public static List<Spot> all() {
		return ObjectSelect.query( Spot.class )
				.select( VacationCore.sharedContext() );
	}

	public static Spot bySlug( final String slug ) {
		return ObjectSelect.query( Spot.class )
				.where( Spot.SLUG.eq( slug ) )
				.selectFirst( VacationCore.sharedContext() );
	}

	/**
	 * @return The spots with a visit planned (or made) on the given trip
	 */
	public static List<Spot> forTrip( final Trip trip ) {
		return trip.visits()
				.stream()
				.map( Visit::spot )
				.filter( Objects::nonNull )
				.distinct()
				.toList();
	}
}
