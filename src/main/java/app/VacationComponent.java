package app;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

public abstract class VacationComponent extends ERXComponent {

	public VacationComponent( WOContext context ) {
		super( context );
	}

	@Override
	public Application application() {
		return (Application)super.application();
	}

	@Override
	public Session session() {
		return (Session)super.session();
	}
}
