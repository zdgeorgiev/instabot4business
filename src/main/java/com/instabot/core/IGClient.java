package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.request.IGCommentsReq;
import com.instabot.core.strategy.NoSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;
import com.instabot.core.util.IGUtils;
import org.brunocvcunha.instagram4j.Instagram4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IGClient {

	private static final int MAX_BEST_USERS_COUNT = 7500;

	private Instagram4j userClient;

	public IGClient(String username, String password) {
		userClient = IGUtils.login(username, password);
	}

	public List<String> getBestUsers(
			List<String> mediaIds) {
		return getBestUsers(mediaIds, null);
	}

	public List<String> getBestUsers(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters) {
		return getBestUsers(mediaIds, filters, NoSortingStrategy.class);
	}

	public List<String> getBestUsers(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy) {
		return getBestUsers(mediaIds, filters, userSortingStrategy, MAX_BEST_USERS_COUNT);
	}

	public List<String> getBestUsers(
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters,
			Class<? extends UserSortingStrategy> userSortingStrategy,
			int maxUsersCount) {

		Map<String, Integer> bestUsersScore =
				mediaIds.stream()
						.map(media -> new IGCommentsReq(this.userClient, media)
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

		return bestUsersScore.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.limit(maxUsersCount)
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
