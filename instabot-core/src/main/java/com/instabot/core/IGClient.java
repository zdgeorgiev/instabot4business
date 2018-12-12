package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.model.IGUser;
import com.instabot.core.request.IGFollowersReq;
import com.instabot.core.request.IGLikesRequest;
import com.instabot.core.request.IGPhotosReq;
import com.instabot.core.strategy.NoSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;
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

	public static void followUsers(IGUser user, List<String> usernames) {
		usernames.forEach(username -> followUser(user, username));
	}

	public static void followUser(IGUser user, String username) {
		new IGFollowersReq(user).followUser(username);
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

	/////////////////////////

	public static List<String> getBestUsersFromComments(
			IGUser user,
			List<String> mediaIds) {
		return getBestUsersFromComments(user, mediaIds, null);
	}

	public static List<String> getBestUsersFromComments(
			IGUser user,
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters) {
		return getBestUsersFromComments(user, mediaIds, filters, NoSortingStrategy.class);
	}

	public static List<String> getBestUsersFromComments(
			IGUser user,
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy) {
		return getBestUsersFromComments(user, mediaIds, filters, userSortingStrategy, MAX_BEST_USERS_COUNT);
	}

	public static List<String> getBestUsersFromComments(
			IGUser user,
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy,
			int bestUsersCount) {

//		LOGGER.info("Creating a list of top {} users from the comments", bestUsersCount);
		//
		//		Map<String, Integer> bestUsersScore =
		//				mediaIds.stream()
		//						.map(media -> new IGCommentsReq(user, media)
		//								.applyFilters(filters)
		//								.applyUserSortingStrategy(userSortingStrategy)
		//								.getComments())
		//						.reduce((a1, a2) -> {
		//									// If same user is present in couple of different medias
		//									// then combine the values of the comments
		//									a2.forEach((k, v) -> a1.merge(k, v, (v1, v2) -> v1 + v2));
		//									return a1;
		//								}
		//						).orElse(Collections.emptyMap());
		//
		//		LOGGER.info("Sorting best users list..");
		//
		//		return bestUsersScore.entrySet().stream()
		//				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
		//				.limit(bestUsersCount)
		//				.collect(Collectors.toList()).stream()
		//				.map(Map.Entry::getKey).collect(Collectors.toList());

		return null;
	}
}
