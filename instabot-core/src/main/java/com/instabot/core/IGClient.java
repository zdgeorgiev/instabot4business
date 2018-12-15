package com.instabot.core;

import com.instabot.core.model.IGUser;
import com.instabot.core.request.IGLikesRequest;

import java.util.List;

public final class IGClient {

	private static final int MAX_USERS_WHO_LIKED_PHOTO_COUNT = 200;

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId) {
		return getUsersWhoLiked(user, mediaId, MAX_USERS_WHO_LIKED_PHOTO_COUNT);
	}

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId, int maxUsersCount) {
		return new IGLikesRequest(user).getUsersWhoLiked(mediaId, maxUsersCount);
	}
}
