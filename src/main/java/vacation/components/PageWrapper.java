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
		return stringValueForBinding( "title", "Búðardalur 2026" );
	}
}
