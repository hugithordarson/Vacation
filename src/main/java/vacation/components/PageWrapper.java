package vacation.components;

import com.webobjects.appserver.WOContext;

import app.VacationComponent;

public class PageWrapper extends VacationComponent {

	public PageWrapper( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public String title() {
		return stringValueForBinding( "title", "Svaka partí" );
	}

	public String mainStyle() {
		return "max-width: %s; margin: 0 auto; padding: 24px 16px".formatted( stringValueForBinding( "width", "720px" ) );
	}
}
