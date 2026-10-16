package app;

import ng.appserver.NGContext;
import ng.appserver.templating.NGComponent;

public abstract class VacationComponent extends NGComponent {

	public VacationComponent( NGContext context ) {
		super( context );
	}

	@Override
	public Application application() {
		return (Application)super.application();
	}
}
