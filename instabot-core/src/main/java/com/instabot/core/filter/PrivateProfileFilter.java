package com.instabot.core.filter;

import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

public class PrivateProfileFilter implements UserFilter {

	@Override
	public InstagramUser apply(InstagramUser user) {
		return user.is_private ? null : user;
	}
}
