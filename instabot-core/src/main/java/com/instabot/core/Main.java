package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.NoPhotoUserFilter;
import com.instabot.core.filter.PublicProfileFilter;
import com.instabot.core.filter.SpamCommentFilter;
import com.instabot.core.model.FakeIGUser;
import com.instabot.core.model.IGUser;
import com.instabot.core.strategy.MostCommentsSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;

import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		IGUser fakeUser = new FakeIGUser("testzdr", "1111112");
		fakeUser.login();

		List<String> mediaIds = IGClient.getUserLastPhotoIds(fakeUser, "witness");

		List<Class<? extends IGFilter>> filters = Arrays.asList(
				NoPhotoUserFilter.class,
				PublicProfileFilter.class,
				SpamCommentFilter.class
		);

		Class<? extends UserSortingStrategy> strategy =
				MostCommentsSortingStrategy.class;

		List<String> bestUsers = IGClient.getBestUsersFromComments(
				fakeUser,
				mediaIds,
				filters,
				strategy,
				20
		);

		IGClient.followUsers(fakeUser, bestUsers);
	}
}