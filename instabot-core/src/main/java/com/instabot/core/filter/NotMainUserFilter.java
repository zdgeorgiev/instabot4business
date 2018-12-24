package com.instabot.core.filter;

import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

public class NotMainUserFilter implements UserFilter {

	// TODO: Not very good approach because if we change the property name it wont work
	// TODO: and this property is used from the api
	private static final String mainUsername = System.getProperty("ig.main.bot.username");

	@Override
	public boolean apply(InstagramUser user) {
		return !user.username.equals(mainUsername);
	}
}
