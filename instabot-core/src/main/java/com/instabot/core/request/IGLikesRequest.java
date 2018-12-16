package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramLikeRequest;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;

public class IGLikesRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGLikesRequest.class);

	private IGUser user;

	public IGLikesRequest(IGUser user) {
		this.user = user;
	}

	public void likePhoto(String mediaId) {
		try {
			user.getInstagram4jIGClient().sendRequest(new InstagramLikeRequest(InstagramCodeUtil.fromCode(mediaId)));
			LOGGER.info("{} user:{} liked photo /p/{}", user.getUserType(), user.getUsername(), mediaId);
		} catch (IOException e) {
			LOGGER.error("Cannot like photo: {}", mediaId, e);
		}
	}

	public List<String> getUsersWhoLiked(String mediaId, int maxUsersCount) {
		// TODO: implement function to return list of usernames that liked specific media id
		throw new NotImplementedException();
	}
}
