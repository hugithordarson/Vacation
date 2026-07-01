package app;

import er.extensions.appserver.ERXSession;

public class Session extends ERXSession {

	public Session() {
		setStoresIDsInCookies( true );
		setStoresIDsInURLs( false );
	}

	@Override
	public String domainForIDCookies() {
		return "/";
	}
}
