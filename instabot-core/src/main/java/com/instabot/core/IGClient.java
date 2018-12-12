package com.instabot.core;

import com.instabot.core.model.IGUser;
import com.instabot.core.request.IGFollowersReq;
import com.instabot.core.request.IGLikesRequest;
import com.instabot.core.request.IGPhotosReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class IGClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGClient.class);

	private static final int MAX_BEST_USERS_COUNT = 750;
	private static final int MAX_USER_PHOTOS_COUNT = 25;
	private static final int MAX_USERS_WHO_LIKED_PHOTO_COUNT = 200;

	private IGClient() {

	}

	/////////////////////////

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId) {
		return getUsersWhoLiked(user, mediaId, MAX_USERS_WHO_LIKED_PHOTO_COUNT);
	}

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId, int maxUsersCount) {
		return new IGLikesRequest(user).getUsersWhoLiked(mediaId, maxUsersCount);
	}

	/////////////////////////

	/////////////////////////

	public static List<String> getUserFollowers(IGUser user, String username) {
		LOGGER.info("Collecting followers for user: {}", username);
		List<String> followers = new ArrayList<>(new IGFollowersReq(user).getFollowers(username));
		LOGGER.info("{} followers were collected", followers.size());
		return followers;
	}

	/////////////////////////

	public static List<String> getUserLastPhotoIds(IGUser user, String username) {
		return getUserLastPhotoIds(user, username, MAX_USER_PHOTOS_COUNT);
	}

	public static List<String> getUserLastPhotoIds(IGUser user, String username, int lastPhotosCount) {
		LOGGER.info("Getting last {} photos for user: {}", lastPhotosCount, username);
		List<String> mediaIds = new ArrayList<>(new IGPhotosReq(user).getMediaIds(username, lastPhotosCount));
		LOGGER.info("{} photo ids were collected", mediaIds.size());
		return mediaIds;
	}
}
