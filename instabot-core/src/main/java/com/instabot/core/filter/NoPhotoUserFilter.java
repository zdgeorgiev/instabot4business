package com.instabot.core.filter;

import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

public class NoPhotoUserFilter implements UserFilter {

	@Override
	public InstagramUser apply(InstagramUser user) {
		return user.has_anonymous_profile_picture ? null : user;
	}
}
