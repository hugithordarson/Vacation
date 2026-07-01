package vacation;

import java.time.LocalDate;
import java.util.List;

/**
 * Hand-coded events for now — will move to the DB along with everything else.
 */

public class Events {

	private static final List<TripEvent> ALL = List.of(
			TripEvent.on( "Ekið vestur: yfir Kaldadal", LocalDate.of( 2026, 7, 12 ), "Lagt af stað frá Hraunteigi — Þingvellir, Kaldidalur, Hraunfossar, Reykholt og ís á Erpsstöðum á leiðinni. 229 km, ~3:36 í akstur." ),
			TripEvent.on( "Innritun í húsið", LocalDate.of( 2026, 7, 12 ), "Húsið er okkar frá og með deginum." ),
			TripEvent.on( "Ísferð á Erpsstaði (tillaga)", LocalDate.of( 2026, 7, 13 ), "Heimagerður ís og dýrin skoðuð." ),
			TripEvent.span( "Vestfjarðaleiðangur (tillaga)", LocalDate.of( 2026, 7, 15 ), LocalDate.of( 2026, 7, 16 ), "Látrabjarg, Rauðisandur og Dynjandi — gist eina nótt fyrir vestan." ),
			TripEvent.on( "Skil á húsinu", LocalDate.of( 2026, 7, 19 ), "Pakkað saman og haldið heim." ) );

	public static List<TripEvent> all() {
		return ALL;
	}

	public static List<TripEvent> on( final LocalDate day ) {
		return ALL
				.stream()
				.filter( event -> event.occursOn( day ) )
				.toList();
	}
}
