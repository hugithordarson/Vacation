package vacation.components;

import java.util.List;

import app.VacationComponent;
import ng.appserver.NGContext;
import vacation.Spots;
import vacation.data.Spot;
import vacation.data.Trip;

public class PhotosPage extends VacationComponent {

	/**
	 * The trip whose spots we show — null shows everything. Set by the route handler.
	 */
	public Trip trip;

	public Spot currentSpot;

	public PhotosPage( NGContext context ) {
		super( context );
	}

	public String heading() {
		return trip == null ? "Myndir af stöðum" : "Myndir af stöðum: " + trip.name();
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
