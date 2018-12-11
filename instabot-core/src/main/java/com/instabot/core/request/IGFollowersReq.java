package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Collection;

public class IGFollowersReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGFollowersReq.class);

	private IGUser user;

	public IGFollowersReq(IGUser user) {
		this.user = user;
	}

	public Collection<String> getFollowers(String username) {
		throw new NotImplementedException();
	}

	public void followUser(String username) {

		long userId;
		try {
			userId = user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		} catch (IOException e) {
			LOGGER.error("Cannot convert user {} to code", username, e);
			throw new RuntimeException();
		}

		try {
			user.getInstagram4jIGClient().sendRequest(new InstagramFollowRequest(userId));
			LOGGER.info("User: {} followed..", username);
		} catch (IOException e) {
			LOGGER.error("Cannot follow user {}", username, e);
		}
	}
}
