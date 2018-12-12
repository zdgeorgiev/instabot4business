package com.instabot.api.model;

import com.instabot.core.strategy.UserSortingStrategy;

public enum UserSortingStrategyType {

	MOST_COMMENT_STRATEGY("MostCommentsSortingStrategy"),
	NO_STRATEGY("NoSortingStrategy");

	private String sortingName;

	UserSortingStrategyType(String sortingName) {
		this.sortingName = sortingName;
	}

	public Class<? extends UserSortingStrategy> getStrategyClass() {
		try {
			return (Class<? extends UserSortingStrategy>) Class.forName("com.instabot.core.strategy." + sortingName);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot init sorting strategy " + this.sortingName, e);
		}
	}
}
