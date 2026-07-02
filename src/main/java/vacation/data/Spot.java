package vacation.data;

import vacation.data.auto._Spot;

/**
 * A place we might want to visit.
 *
 * status: null = part of the plan, "kannski" = maybe, "tillaga" = an unsorted suggestion
 */

public class Spot extends _Spot {

	private static final long serialVersionUID = 1L;

	public boolean maybe() {
		return "kannski".equals( status() );
	}

	public boolean suggestion() {
		return "tillaga".equals( status() );
	}
}
