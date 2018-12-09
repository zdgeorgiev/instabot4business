package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.NoPhotoUserFilter;
import com.instabot.core.filter.PublicProfileFilter;
import com.instabot.core.filter.SpamCommentFilter;
import com.instabot.core.strategy.MostCommentsSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;

import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		List<String> mediaIds = IGClient.getUserLastPhotoIds("witness");

		List<Class<? extends IGFilter>> filters = Arrays.asList(
				NoPhotoUserFilter.class,
				PublicProfileFilter.class,
				SpamCommentFilter.class
		);

		Class<? extends UserSortingStrategy> strategy =
				MostCommentsSortingStrategy.class;

		List<String> bestUsers = IGClient.getBestUsersFromComments(
				mediaIds,
				filters,
				strategy,
				20
		);

		IGClient.followUsers(bestUsers);
	}
}