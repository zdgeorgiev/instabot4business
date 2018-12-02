package com.instabot.core.strategy;

public class MostCommentsSortingStrategy implements UserSortingStrategy {

	@Override
	public int apply(String comment) {
		return 1;
	}
}
