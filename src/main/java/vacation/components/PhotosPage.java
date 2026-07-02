package vacation.components;

import java.util.List;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Spot;
import vacation.Spots;

public class PhotosPage extends VacationComponent {

	public Spot currentSpot;

	public PhotosPage( WOContext context ) {
		super( context );
	}

	public List<Spot> spotsWithImages() {
		return Spots.all()
				.stream()
				.filter( spot -> spot.image() != null )
				.toList();
	}

	public String currentSpotMapLink() {
		return "/map/?spot=" + currentSpot.slug();
	}
}
