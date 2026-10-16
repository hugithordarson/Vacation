package vacation.components;

import java.util.Objects;

import app.VacationComponent;
import ng.appserver.NGContext;

public class PageWrapper extends VacationComponent {

	public PageWrapper( NGContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public String title() {
		return Objects.requireNonNullElse( (String)valueForBinding( "title" ), "Svaka partí" );
	}

	public String mainStyle() {
		return "max-width: %s; margin: 0 auto; padding: 24px 16px".formatted( Objects.requireNonNullElse( (String)valueForBinding( "width" ), "720px" ) );
	}
}
