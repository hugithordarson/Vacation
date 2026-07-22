package vacation.components;

import java.util.Comparator;
import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Spots;
import vacation.Trips;
import vacation.data.Spot;
import vacation.data.Trip;

/**
 * The overview: all trips grouped by lifecycle — in planning, ideas, and completed.
 * Everything trip-specific lives on the trip's own page.
 */

public class FrontPage extends VacationComponent {

	public Trip currentTrip;

	public FrontPage( WOContext context ) {
		super( context );
	}

	public List<Trip> planningTrips() {
		return Trips.all()
				.stream()
				.filter( Trip::planning )
				.toList();
	}

	public List<Trip> ideaTrips() {
		return Trips.all()
				.stream()
				.filter( trip -> "idea".equals( trip.status() ) )
				.toList();
	}

	public List<Trip> doneTrips() {
		return Trips.all()
				.stream()
				.filter( trip -> "done".equals( trip.status() ) )
				.sorted( Comparator.comparing( Trip::start ).reversed() )
				.toList();
	}

	public boolean hasPlanningTrips() {
		return !planningTrips().isEmpty();
	}

	public boolean hasIdeaTrips() {
		return !ideaTrips().isEmpty();
	}

	public boolean hasDoneTrips() {
		return !doneTrips().isEmpty();
	}

	public String currentTripLink() {
		return "/trip/" + currentTrip.slug();
	}

	/**
	 * @return The trip's lodging image, for the planning-trip cards
	 */
	public String currentTripImage() {
		return Spots.forTrip( currentTrip )
				.stream()
				.filter( spot -> "Gisting".equals( spot.category() ) )
				.map( Spot::image )
				.filter( image -> image != null )
				.findFirst()
				.orElse( null );
	}

	public boolean currentTripHasImage() {
		return currentTripImage() != null;
	}
}
