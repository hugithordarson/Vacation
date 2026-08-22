package vacation.components;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.SharedAlbum;
import vacation.Spots;
import vacation.data.DrivingRoute;
import vacation.data.Spot;
import vacation.data.Trip;

/**
 * A trip's home page: its map, driving routes, lodging and links to its calendar and photos.
 */

public class TripPage extends VacationComponent {

	/**
	 * Set by the route handler
	 */
	public Trip trip;

	public DrivingRoute currentRoute;

	public TripPage( WOContext context ) {
		super( context );
	}

	public List<DrivingRoute> routes() {
		return trip.routes();
	}

	public boolean hasRoutes() {
		return !routes().isEmpty();
	}

	public String currentRouteLink() {
		return "/route/" + currentRoute.slug();
	}

	public String currentRouteDotStyle() {
		return "color: " + currentRoute.color();
	}

	public String mapLink() {
		return "/map/" + trip.slug();
	}

	public String calendarLink() {
		return "/calendar/" + trip.slug();
	}

	public String photosLink() {
		return "/photos/" + trip.slug();
	}

	public Spot currentImageSpot;
	public SharedAlbum.AlbumPhoto currentAlbumPhoto;

	/**
	 * @return Photos from the trip's linked iCloud shared album (empty when none linked or iCloud unreachable)
	 */
	public List<SharedAlbum.AlbumPhoto> albumPhotos() {
		return SharedAlbum.photos( trip.sharedAlbumToken() );
	}

	public boolean hasAlbumPhotos() {
		return !albumPhotos().isEmpty();
	}

	/**
	 * @return True when the trip has an album linked but its photos haven't arrived from iCloud yet
	 */
	public boolean albumLoading() {
		return trip.sharedAlbumToken() != null && albumPhotos().isEmpty();
	}

	public List<SharedAlbum.AlbumPhoto> albumPreview() {
		return albumPhotos()
				.stream()
				.limit( 12 )
				.toList();
	}

	public int albumPhotoCount() {
		return albumPhotos().size();
	}

	/**
	 * @return The trip's spots that have an image
	 */
	public List<Spot> imageSpots() {
		return Spots.forTrip( trip )
				.stream()
				.filter( spot -> spot.image() != null )
				.toList();
	}

	public boolean hasImages() {
		return !imageSpots().isEmpty();
	}

	public List<Spot> imagePreview() {
		return imageSpots()
				.stream()
				.limit( 6 )
				.toList();
	}

	public int imageCount() {
		return imageSpots().size();
	}

	public Spot currentGisting;

	private static final DateTimeFormatter STAY_FORMATTER = DateTimeFormatter.ofPattern( "d. MMMM", Locale.of( "is" ) );

	/**
	 * @return The trip's lodgings — its "Gisting"-category spots, in visit order (a trip can rent several properties)
	 */
	public List<Spot> gistings() {
		return Spots.forTrip( trip )
				.stream()
				.filter( spot -> "Gisting".equals( spot.category() ) )
				.toList();
	}

	public boolean hasGistings() {
		return !gistings().isEmpty();
	}

	public String currentGistingLink() {
		return "/spot/" + currentGisting.slug();
	}

	/**
	 * @return The stay span of the current lodging on this trip, e.g. "31. júlí – 1. ágúst" — null when the visit is undated
	 */
	public String currentGistingStayLabel() {
		return currentGisting.visits()
				.stream()
				.filter( visit -> trip.equals( visit.trip() ) && visit.date() != null )
				.findFirst()
				.map( visit -> visit.end() == null || visit.end().equals( visit.date() )
						? STAY_FORMATTER.format( visit.date() )
						: STAY_FORMATTER.format( visit.date() ) + " – " + STAY_FORMATTER.format( visit.end() ) )
				.orElse( null );
	}
}
