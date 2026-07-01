package vacation;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import er.extensions.appserver.ERXApplication;
import er.extensions.foundation.ERXUtilities;

public class Spots {

	private static List<Spot> _spots;

	public static List<Spot> all() {

		// In development we reload on every access, so spots.json can be edited while the app runs
		if( _spots == null || ERXApplication.erxApplication().isDevelopmentMode() ) {
			final String jsonString = ERXUtilities.readStringFromBundleResource( "spots.json", null, null, StandardCharsets.UTF_8 );
			final Type type = new TypeToken<List<Spot>>() {}.getType();
			_spots = new GsonBuilder().create().fromJson( jsonString, type );
		}

		return _spots;
	}

	public static Spot bySlug( final String slug ) {
		return all()
				.stream()
				.filter( spot -> spot.slug().equals( slug ) )
				.findFirst()
				.orElse( null );
	}

	public static String asJSON() {
		return new GsonBuilder().create().toJson( all() );
	}
}
