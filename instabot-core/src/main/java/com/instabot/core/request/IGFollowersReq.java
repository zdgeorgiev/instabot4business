package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;

import java.io.IOException;

public class IGFollowersReq {

	private IGUser user;

	public IGFollowersReq(IGUser user) {
		this.user = user;
	}

	public void followUser(String username) throws IOException {
		long userId = user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		user.getInstagram4jIGClient().sendRequest(new InstagramFollowRequest(userId));
	}

	public void unfollow(String username) throws Exception {
		long userId = user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		user.getInstagram4jIGClient().sendRequest(new InstagramUnfollowRequest(userId));
	}
}
