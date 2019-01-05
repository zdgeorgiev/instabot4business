package com.instabot.core.model;

import org.openqa.selenium.remote.RemoteWebDriver;

public final class MainIGUser extends IGUser {

	public MainIGUser(String username, String password) {
		super(username, password, UserType.MAIN);
	}

	// Everything we make with the main user is :
	// - like photo
	// - follow user
	// and both are available through the instagram4j library, so
	// there is no need to use a selenium client
	@Override
	protected RemoteWebDriver initSeleniumIGClient() {
		return null;
	}
}
