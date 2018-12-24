package com.instabot.api.service;

import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGLikesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class InstagramLikeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGLikesRequest.class);

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);

	public void likePhoto(String mediaId) {
		try {
			new IGLikesRequest(mainIGUser).likePhoto(mediaId);
			LOGGER.info("{} user:{} liked photo /p/{}", mainIGUser.getUserType(), mainIGUser.getUsername(), mediaId);
		} catch (IOException e) {
			LOGGER.error("Cannot like photo {}", mediaId, e);
			throw new RuntimeException(e);
		}
	}

	public void addTopTargetLikers(String username) {
		//TODO: implement function to return top likers for username
	}
}
