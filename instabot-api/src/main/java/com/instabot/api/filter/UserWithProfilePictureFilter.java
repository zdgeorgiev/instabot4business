package com.instabot.api.filter;

import com.instabot.core.filter.UserFilter;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

public class UserWithProfilePictureFilter implements UserFilter {

	@Override
	public boolean apply(InstagramUser user) {
		return !user.has_anonymous_profile_picture;
	}
}
