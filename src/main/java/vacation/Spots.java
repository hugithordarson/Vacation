package vacation;

import java.util.List;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import vacation.data.Spot;

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
}
