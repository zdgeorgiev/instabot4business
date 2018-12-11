package com.instabot.core.model;

public final class FakeIGUser extends IGUser {

	public FakeIGUser(String username, String password) {
		super(username, password, UserType.FAKE);
	}

}
