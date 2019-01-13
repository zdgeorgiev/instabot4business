package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

import java.io.IOException;

public class IGFollowersReq {

	private IGUser user;

	public IGFollowersReq(IGUser user) {
		this.user = user;
	}

	public void followUser(String username) throws IOException {
		InstagramUser user = this.user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser();

		if (user == null) {
			throw new IllegalStateException("User " + username + " is no longer available.");
		}

		this.user.getInstagram4jIGClient().sendRequest(new InstagramFollowRequest(user.pk));
	}

	public void unfollow(String username) throws Exception {
		InstagramUser user = this.user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser();

		if (user == null) {
			throw new IllegalStateException("User " + username + " is no longer available.");
		}

		this.user.getInstagram4jIGClient().sendRequest(new InstagramUnfollowRequest(user.pk));
	}
}
