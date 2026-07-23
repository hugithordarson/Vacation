package vacation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Reads the photos of an iCloud Shared Album through its "Public Website" webstream —
 * the endpoint behind icloud.com/sharedalbum/#token. Not an official API, but stable
 * for over a decade and read-only; if Apple ever changes it, the album link itself
 * still works and we just lose the inline thumbnails.
 *
 * The dance: (1) POST to sharedstreams.icloud.com resolves the partition host (HTTP 330
 * with X-Apple-MMe-Host in the body), (2) POST webstream lists photos and their derivative
 * sizes, (3) POST webasseturls resolves short-lived download URLs for chosen derivatives.
 * Results are cached for a few minutes since the asset URLs expire anyway.
 */

public class SharedAlbum {

	private static final Logger logger = LoggerFactory.getLogger( SharedAlbum.class );

	private static final Duration CACHE_TTL = Duration.ofMinutes( 10 );

	private static final HttpClient client = HttpClient.newBuilder().connectTimeout( Duration.ofSeconds( 5 ) ).build();
	private static final Gson gson = new GsonBuilder().create();

	private static final Map<String, CachedAlbum> cache = new ConcurrentHashMap<>();

	public record AlbumPhoto( String guid, String url, int width, int height, String caption ) {}

	private record CachedAlbum( List<AlbumPhoto> photos, long fetchedAt ) {}

	/**
	 * @return The album's photos, oldest first — empty on any failure (logged), so pages degrade gracefully
	 */
	public static List<AlbumPhoto> photos( final String token ) {

		if( token == null || token.isEmpty() ) {
			return List.of();
		}

		final CachedAlbum cached = cache.get( token );

		if( cached != null && System.currentTimeMillis() - cached.fetchedAt() < CACHE_TTL.toMillis() ) {
			return cached.photos();
		}

		try {
			final List<AlbumPhoto> photos = fetch( token );
			cache.put( token, new CachedAlbum( photos, System.currentTimeMillis() ) );
			return photos;
		}
		catch( final Exception e ) {
			logger.warn( "Failed to fetch shared album {}: {}", token, e.toString() );

			// Keep serving stale data if we have it — better than an empty strip
			return cached != null ? cached.photos() : List.of();
		}
	}

	@SuppressWarnings("unchecked")
	private static List<AlbumPhoto> fetch( final String token ) throws Exception {
		final String host = resolveHost( token );

		final Map<String, Object> stream = postJSON( "https://%s/%s/sharedstreams/webstream".formatted( host, token ), "{\"streamCtag\":null}" );
		final List<Map<String, Object>> photoList = (List<Map<String, Object>>)stream.get( "photos" );

		if( photoList == null ) {
			return List.of();
		}

		// Pick a sensible derivative per photo: the smallest one at least ~800px wide (or the largest available)
		record Chosen( String guid, String checksum, int width, int height, String caption ) {}
		final List<Chosen> chosen = new ArrayList<>();

		for( final Map<String, Object> photo : photoList ) {
			final Map<String, Map<String, Object>> derivatives = (Map<String, Map<String, Object>>)photo.get( "derivatives" );

			if( derivatives == null || derivatives.isEmpty() ) {
				continue;
			}

			final List<Map<String, Object>> sorted = derivatives.values()
					.stream()
					.sorted( Comparator.comparingInt( d -> intOf( d.get( "width" ) ) ) )
					.toList();

			final Map<String, Object> pick = sorted.stream()
					.filter( d -> intOf( d.get( "width" ) ) >= 800 )
					.findFirst()
					.orElse( sorted.getLast() );

			chosen.add( new Chosen(
					(String)photo.get( "photoGuid" ),
					(String)pick.get( "checksum" ),
					intOf( pick.get( "width" ) ),
					intOf( pick.get( "height" ) ),
					(String)photo.get( "caption" ) ) );
		}

		// Resolve download URLs for the chosen derivatives
		final String guidsJSON = gson.toJson( Map.of( "photoGuids", chosen.stream().map( Chosen::guid ).toList() ) );
		final Map<String, Object> assets = postJSON( "https://%s/%s/sharedstreams/webasseturls".formatted( host, token ), guidsJSON );
		final Map<String, Map<String, Object>> items = (Map<String, Map<String, Object>>)assets.get( "items" );

		final List<AlbumPhoto> result = new ArrayList<>();

		for( final Chosen c : chosen ) {
			final Map<String, Object> item = items == null ? null : items.get( c.checksum() );

			if( item == null ) {
				continue;
			}

			final String url = "https://" + item.get( "url_location" ) + item.get( "url_path" );
			result.add( new AlbumPhoto( c.guid(), url, c.width(), c.height(), c.caption() ) );
		}

		return result;
	}

	/**
	 * @return The partition host serving this album (e.g. p42-sharedstreams.icloud.com)
	 */
	private static String resolveHost( final String token ) throws Exception {
		final HttpRequest request = HttpRequest.newBuilder()
				.uri( URI.create( "https://sharedstreams.icloud.com/%s/sharedstreams/webstream".formatted( token ) ) )
				.timeout( Duration.ofSeconds( 10 ) )
				.header( "Content-Type", "text/plain" )
				.POST( HttpRequest.BodyPublishers.ofString( "{\"streamCtag\":null}" ) )
				.build();

		final HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
		final Map<String, Object> body = gson.fromJson( response.body(), Map.class );
		final String host = body == null ? null : (String)body.get( "X-Apple-MMe-Host" );
		return host != null ? host : "sharedstreams.icloud.com";
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> postJSON( final String url, final String body ) throws Exception {
		final HttpRequest request = HttpRequest.newBuilder()
				.uri( URI.create( url ) )
				.timeout( Duration.ofSeconds( 10 ) )
				.header( "Content-Type", "text/plain" )
				.POST( HttpRequest.BodyPublishers.ofString( body ) )
				.build();

		final HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );

		if( response.statusCode() != 200 ) {
			throw new RuntimeException( "HTTP %d from %s".formatted( response.statusCode(), url ) );
		}

		return gson.fromJson( response.body(), Map.class );
	}

	private static int intOf( final Object value ) {
		if( value instanceof Number n ) {
			return n.intValue();
		}
		return value == null ? 0 : (int)Double.parseDouble( value.toString() );
	}
}
