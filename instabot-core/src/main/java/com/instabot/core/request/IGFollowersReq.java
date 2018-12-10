package com.instabot.core.request;

import com.instabot.core.client.instagram4j.Instagram4jIG;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Collection;

public class IGFollowersReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGFollowersReq.class);

	private Instagram4j userClient;

	public IGFollowersReq() {
		this.userClient = Instagram4jIG.getClient();
	}

	public Collection<String> getFollowers(String username) {
		throw new NotImplementedException();
	}

	public void followUser(String username) {

		long userId;
		try {
			userId = userClient.sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		} catch (IOException e) {
			LOGGER.error("Cannot convert user {} to code", username, e);
			throw new RuntimeException();
		}

		try {
			userClient.sendRequest(new InstagramFollowRequest(userId));
			LOGGER.info("User: {} followed..", username);
		} catch (IOException e) {
			LOGGER.error("Cannot follow user {}", username, e);
		}
	}
}
