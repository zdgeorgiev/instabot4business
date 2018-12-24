package com.instabot.core;

import com.instabot.core.model.IGUser;
import com.instabot.core.request.IGLikesRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class IGClient {

	private static final int MAX_USERS_WHO_LIKED_PHOTO_COUNT = 200;

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId) {
		return getUsersWhoLiked(user, mediaId, MAX_USERS_WHO_LIKED_PHOTO_COUNT);
	}

	public static List<String> getUsersWhoLiked(IGUser user, String mediaId, int maxUsersCount) {
		return new IGLikesRequest(user).getUsersWhoLiked(mediaId, maxUsersCount);
	}

	public static void main(String[] args) throws InterruptedException {

		List<LocalDateTime> list = new ArrayList<>();
		list.add(LocalDateTime.now());
		Thread.sleep(100);
		list.add(LocalDateTime.now());
		Thread.sleep(100);
		list.add(LocalDateTime.now());

		list.stream()
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
	}
}
