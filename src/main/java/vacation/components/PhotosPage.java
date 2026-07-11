package vacation.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Spots;
import vacation.data.Spot;
import vacation.data.Trip;

public class PhotosPage extends VacationComponent {

	/**
	 * The trip whose spots we show — null shows everything. Set by the route handler.
	 */
	public Trip trip;

	public Spot currentSpot;

	public PhotosPage( WOContext context ) {
		super( context );
	}

	public String heading() {
		return trip == null ? "Myndirnar" : "Myndirnar: " + trip.name();
	}

	public List<Spot> spotsWithImages() {
		return (trip == null ? Spots.all() : Spots.forTrip( trip ))
				.stream()
				.filter( spot -> spot.image() != null )
				.toList();
	}

	public String currentSpotMapLink() {
		return "/map/?spot=" + currentSpot.slug();
	}
}
