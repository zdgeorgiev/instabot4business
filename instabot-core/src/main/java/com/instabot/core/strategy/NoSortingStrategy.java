package com.instabot.core.strategy;

public class NoSortingStrategy implements UserSortingStrategy {

	@Override
	public int apply(String comment) {
		return 0;
	}
}
