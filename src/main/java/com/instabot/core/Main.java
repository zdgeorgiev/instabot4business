package com.instabot.core;

import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.NoPhotoUserFilter;
import com.instabot.core.filter.SpamCommentFilter;
import com.instabot.core.strategy.MostCommentsSortingStrategy;
import com.instabot.core.strategy.UserSortingStrategy;

import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		IGClient client = new IGClient("testzdr", "1111112");

		List<String> mediaIds = Arrays.asList(
				"Bq5IVwrBqx_",
				"Bq3sFHthTPH",
				"Bq2ruKNBIyB",
				"Bq0hGnPhtG7",
				"BqyjaJFBwlJ"
		);

		List<Class<? extends IGFilter>> filters = Arrays.asList(
				NoPhotoUserFilter.class,
				SpamCommentFilter.class
		);

		Class<? extends UserSortingStrategy> strategy =
				MostCommentsSortingStrategy.class;

		List<String> bestUsers = client.getBestUsers(
				mediaIds,
				filters,
				strategy,
				20
		);

		System.out.println(bestUsers);
	}
}