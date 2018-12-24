package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramFollowRequest;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsernameRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUnfollowRequest;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.Collection;

public class IGFollowersReq {

	private IGUser user;

	public IGFollowersReq(IGUser user) {
		this.user = user;
	}

	public void followUser(String username) throws IOException {
		long userId = user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		user.getInstagram4jIGClient().sendRequest(new InstagramFollowRequest(userId));
	}

	public Collection<String> getFollowers(String username) throws Exception {
		// TODO: implement function to get list of followers for a user
		throw new NotImplementedException();
	}

	public void unfollow(String username) throws Exception {
		long userId = user.getInstagram4jIGClient().sendRequest(new InstagramSearchUsernameRequest(username)).getUser().pk;
		user.getInstagram4jIGClient().sendRequest(new InstagramUnfollowRequest(userId));
	}
}
