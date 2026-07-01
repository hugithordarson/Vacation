package vacation;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import er.extensions.appserver.ERXApplication;
import er.extensions.foundation.ERXUtilities;

public class Routes {

	private static List<DrivingRoute> _routes;

	public static List<DrivingRoute> all() {

		// In development we reload on every access, so routes.json can be edited while the app runs
		if( _routes == null || ERXApplication.erxApplication().isDevelopmentMode() ) {
			final String jsonString = ERXUtilities.readStringFromBundleResource( "routes.json", null, null, StandardCharsets.UTF_8 );
			final Type type = new TypeToken<List<DrivingRoute>>() {}.getType();
			_routes = new GsonBuilder().create().fromJson( jsonString, type );
		}

		return _routes;
	}

	public static DrivingRoute bySlug( final String slug ) {
		return all()
				.stream()
				.filter( route -> route.slug().equals( slug ) )
				.findFirst()
				.orElse( null );
	}

	public static String asJSON() {
		return new GsonBuilder().create().toJson( all() );
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
