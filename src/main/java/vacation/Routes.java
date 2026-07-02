package vacation;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.cayenne.query.ObjectSelect;

import app.VacationCore;
import er.extensions.foundation.ERXUtilities;
import vacation.data.DrivingRoute;

public class Routes {

	public static List<DrivingRoute> all() {
		return ObjectSelect.query( DrivingRoute.class )
				.select( VacationCore.sharedContext() );
	}

	public static DrivingRoute bySlug( final String slug ) {
		return ObjectSelect.query( DrivingRoute.class )
				.where( DrivingRoute.SLUG.eq( slug ) )
				.selectFirst( VacationCore.sharedContext() );
	}

	/**
	 * @return The cached OSRM geometry document for the given route ({distanceKm, durationMin, geometry}), or null if the route doesn't exist
	 */
	public static String geometryJSON( final String slug ) {

		if( bySlug( slug ) == null ) {
			return null;
		}

		return ERXUtilities.readStringFromBundleResource( "routes/" + slug + ".geojson", null, null, StandardCharsets.UTF_8 );
	}
}
