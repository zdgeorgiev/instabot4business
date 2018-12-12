package com.instabot.api.service;

import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGLikesRequest;
import org.springframework.stereotype.Service;

@Service
public class InstagramLikeService {

	public void likePhoto(String mediaId) {
		new IGLikesRequest(UsersPoolFactory.getUser(UserType.MAIN)).likePhoto(mediaId);
	}
}
