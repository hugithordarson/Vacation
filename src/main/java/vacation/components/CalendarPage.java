package vacation.components;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;
import vacation.Events;
import vacation.Trips;
import vacation.data.Trip;
import vacation.data.CalendarEvent;

/**
 * Month calendar. Two modes:
 *
 * - trip == null: "allt planið" — every trip shown as a spanning bar (linking to its own calendar) plus all events
 * - trip set: that trip and its associated events only, trip days tinted
 *
 * Multi-day bars are segmented per week row in Java (a bar continuing into the next week
 * gets a squared-off edge), keeping the template free of calendar math.
 */

public class CalendarPage extends VacationComponent {

	private static final DateTimeFormatter MONTH_TITLE_FORMATTER = DateTimeFormatter.ofPattern( "MMMM yyyy", Locale.of( "is" ) );

	private static final String TRIP_BAR_COLOR = "#14385c";
	private static final String EVENT_BAR_COLOR = "#1f78b4";
	private static final String STANDALONE_BAR_COLOR = "#6a3d9a";

	/**
	 * The trip whose calendar we're showing — null shows the whole plan. Set by the route handler.
	 */
	public Trip trip;

	/**
	 * The month shown. Set by the route handler.
	 */
	public YearMonth month;

	public Week currentWeek;
	public Bar currentBar;
	public DayCell currentCell;
	public CalendarEvent currentEvent;
	public String currentDayName;

	public CalendarPage( WOContext context ) {
		super( context );
	}

	/**
	 * A bar segment within one week row (a trip or a multi-day event)
	 */
	public record Bar( String title, String label, String style, String href, String tooltip ) {

		public boolean linked() {
			return href != null;
		}
	}

	/**
	 * One day in the month grid: a number cell (top row of the week) and a content cell holding the day's events
	 */
	public record DayCell( String number, String numberStyle, String contentStyle, List<CalendarEvent> events ) {}

	public record Week( List<Bar> bars, List<DayCell> cells ) {}

	public boolean allMode() {
		return trip == null;
	}

	public String pageTitle() {
		return allMode() ? "Fjölskyldudagatalið" : trip.name();
	}

	public String heading() {
		return allMode() ? "Fjölskyldudagatalið" : "Dagatalið: " + trip.name();
	}

	public String monthTitle() {
		final String label = MONTH_TITLE_FORMATTER.format( month );
		return label.substring( 0, 1 ).toUpperCase() + label.substring( 1 );
	}

	private String baseLink() {
		return "/calendar/" + (allMode() ? "" : trip.slug());
	}

	public String prevMonthLink() {
		return baseLink() + "?month=" + month.minusMonths( 1 );
	}

	public String nextMonthLink() {
		return baseLink() + "?month=" + month.plusMonths( 1 );
	}

	public List<String> dayNames() {
		return List.of( "Mán", "Þri", "Mið", "Fim", "Fös", "Lau", "Sun" );
	}

	/**
	 * The events in scope: the trip's own in trip mode, everything (incl. standalone) in all mode
	 */
	private List<CalendarEvent> scopedEvents() {
		return allMode() ? Events.all() : trip.events();
	}

	/**
	 * Everything rendered as a spanning bar: trips first, then multi-day events
	 */
	private record BarSource( String title, String label, LocalDate start, LocalDate end, String color, String href, String tooltip ) {}

	private List<BarSource> barSources() {
		final List<BarSource> result = new ArrayList<>();

		for( final Trip t : allMode() ? Trips.all() : List.of( trip ) ) {
			result.add( new BarSource( t.name(), t.statusLabel(), t.start(), t.end(), TRIP_BAR_COLOR, "/calendar/" + t.slug(), t.description() ) );
		}

		for( final CalendarEvent event : scopedEvents() ) {
			if( event.period() ) {
				final String color = event.trip() == null ? STANDALONE_BAR_COLOR : EVENT_BAR_COLOR;
				final String label = (event.person() != null ? event.person() + " · " : "") + event.spanLabel();
				result.add( new BarSource( event.title(), label, event.start(), event.end(), color, hrefFor( event ), event.description() ) );
			}
		}

		result.sort( ( a, b ) -> a.start().compareTo( b.start() ) );
		return result;
	}

	public List<Week> weeks() {
		final LocalDate gridStart = month.atDay( 1 ).with( TemporalAdjusters.previousOrSame( DayOfWeek.MONDAY ) );
		final LocalDate gridEnd = month.atEndOfMonth().with( TemporalAdjusters.nextOrSame( DayOfWeek.SUNDAY ) );
		final List<BarSource> sources = barSources();

		final List<Week> result = new ArrayList<>();

		for( LocalDate weekStart = gridStart; !weekStart.isAfter( gridEnd ); weekStart = weekStart.plusDays( 7 ) ) {
			final LocalDate weekEnd = weekStart.plusDays( 6 );

			// Bar segments: each source overlapping this week gets its own grid row below the number row
			final List<Bar> bars = new ArrayList<>();
			int row = 2;

			for( final BarSource source : sources ) {

				if( source.end().isBefore( weekStart ) || source.start().isAfter( weekEnd ) ) {
					continue;
				}

				final boolean continuesLeft = source.start().isBefore( weekStart );
				final boolean continuesRight = source.end().isAfter( weekEnd );
				final LocalDate segmentStart = continuesLeft ? weekStart : source.start();
				final LocalDate segmentEnd = continuesRight ? weekEnd : source.end();
				final int columnStart = (int)ChronoUnit.DAYS.between( weekStart, segmentStart ) + 1;
				final int columnEnd = (int)ChronoUnit.DAYS.between( weekStart, segmentEnd ) + 2;

				final String radius = "%1$s %2$s %2$s %1$s".formatted( continuesLeft ? "0" : "6px", continuesRight ? "0" : "6px" );
				final String style = ("grid-column: %d / %d; grid-row: %d; background: %s; border-radius: %s; "
						+ "color: #fff; padding: 4px 8px; font-size: 0.8em; white-space: nowrap; overflow: hidden; text-overflow: ellipsis")
								.formatted( columnStart, columnEnd, row++, source.color(), radius );

				bars.add( new Bar( source.title(), source.label(), style, source.href(), source.tooltip() ) );
			}

			// Day cells: number cell in row 1, content cell in the row below the bars
			final int contentRow = row;
			final List<DayCell> cells = new ArrayList<>();

			for( int i = 0; i < 7; i++ ) {
				final LocalDate day = weekStart.plusDays( i );
				final boolean inMonth = YearMonth.from( day ).equals( month );
				final boolean inTrip = !allMode() && !day.isBefore( trip.start() ) && !day.isAfter( trip.end() );

				final String background = !inMonth ? "#fcfcfc" : inTrip ? "#e3edf7" : "#f4f6f8";
				final String numberColor = inMonth ? "#333" : "#bbb";
				final int column = i + 1;

				final String numberStyle = "grid-column: %d; grid-row: 1; background: %s; color: %s; padding: 4px 8px; font-size: 0.8em; font-weight: 600; border-radius: 6px 6px 0 0"
						.formatted( column, background, numberColor );
				final String contentStyle = "grid-column: %d; grid-row: %d; background: %s; min-height: 64px; padding: 4px; border-radius: 0 0 6px 6px"
						.formatted( column, contentRow, background );

				final List<CalendarEvent> dayEvents = scopedEvents()
						.stream()
						.filter( event -> !event.period() )
						.filter( event -> event.occursOn( day ) )
						.toList();

				cells.add( new DayCell( String.valueOf( day.getDayOfMonth() ), numberStyle, contentStyle, dayEvents ) );
			}

			result.add( new Week( bars, cells ) );
		}

		return result;
	}

	private static String hrefFor( final CalendarEvent event ) {

		if( event.spot() != null ) {
			return "/spot/" + event.spot().slug();
		}

		if( event.route() != null ) {
			return "/route/" + event.route().slug();
		}

		return null;
	}

	public String currentEventHref() {
		return hrefFor( currentEvent );
	}

	public boolean currentEventHasLink() {
		return currentEventHref() != null;
	}
}
