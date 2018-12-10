package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.request.IGCommentsReq;
import com.instabot.core.request.IGFollowersReq;
import com.instabot.core.request.IGLikesRequest;
import com.instabot.core.request.IGPhotosReq;
import com.instabot.core.strategy.NoSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class IGClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGClient.class);

	private static final int MAX_BEST_USERS_COUNT = 750;
	private static final int MAX_USER_PHOTOS_COUNT = 25;
	private static final int MAX_USERS_WHO_LIKED_PHOTO_COUNT = 200;

	private IGClient() {

	}

	/////////////////////////

	public static List<String> getUsersWhoLiked(String mediaId) {
		return getUsersWhoLiked(mediaId, MAX_USERS_WHO_LIKED_PHOTO_COUNT);
	}

	public static List<String> getUsersWhoLiked(String mediaId, int maxUsersCount) {
		return new IGLikesRequest().getUsersWhoLiked(mediaId, maxUsersCount);
	}

	/////////////////////////

	public static void likePhotos(List<String> mediaIds) {
		mediaIds.forEach(IGClient::likePhoto);
	}

	public static void likePhoto(String mediaId) {
		new IGLikesRequest().likePhoto(mediaId);
	}

	/////////////////////////

	public static List<String> getUserFollowers(String username) {
		LOGGER.info("Collecting followers for user: {}", username);
		List<String> followers = new ArrayList<>(new IGFollowersReq().getFollowers(username));
		LOGGER.info("{} followers were collected", followers.size());
		return followers;
	}

	/////////////////////////

	public static void followUsers(List<String> usernames) {
		usernames.forEach(IGClient::followUser);
	}

	public static void followUser(String username) {
		new IGFollowersReq().followUser(username);
	}

	/////////////////////////

	public static List<String> getUserLastPhotoIds(String username) {
		return new ArrayList<>(new IGPhotosReq().getMediaIds(username, MAX_USER_PHOTOS_COUNT));
	}

	public static List<String> getUserLastPhotoIds(String username, int lastPhotosCount) {
		LOGGER.info("Getting last {} photos for user: {}", lastPhotosCount, username);
		List<String> mediaIds = new ArrayList<>(new IGPhotosReq().getMediaIds(username, lastPhotosCount));
		LOGGER.info("{} photo ids were collected", mediaIds.size());
		return mediaIds;
	}

	/////////////////////////

	public static List<String> getBestUsersFromComments(
			List<String> mediaIds) {
		return getBestUsersFromComments(mediaIds, null);
	}

	public static List<String> getBestUsersFromComments(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters) {
		return getBestUsersFromComments(mediaIds, filters, NoSortingStrategy.class);
	}

	public static List<String> getBestUsersFromComments(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy) {
		return getBestUsersFromComments(mediaIds, filters, userSortingStrategy, MAX_BEST_USERS_COUNT);
	}

	public static List<String> getBestUsersFromComments(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy,
			int bestUsersCount) {

		LOGGER.info("Creating a list of top {} users from the comments", bestUsersCount);

		Map<String, Integer> bestUsersScore =
				mediaIds.stream()
						.map(media -> new IGCommentsReq(media)
								.applyFilters(filters)
								.applyUserSortingStrategy(userSortingStrategy)
								.execute())
						.reduce((a1, a2) -> {
									// If same user is present in couple of different medias
									// then combine the values of the comments
									a2.forEach((k, v) -> a1.merge(k, v, (v1, v2) -> v1 + v2));
									return a1;
								}
						).orElse(Collections.emptyMap());

		LOGGER.info("Sorting best users list..");

		return bestUsersScore.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.limit(bestUsersCount)
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
