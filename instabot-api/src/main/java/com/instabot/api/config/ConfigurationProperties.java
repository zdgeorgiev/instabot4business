package com.instabot.api.config;

public class ConfigurationProperties {

	public static final String MAIN_IG_USERNAME = System.getProperty("ig.main.bot.username");
	public static final String MAIN_IG_PASSWORD = System.getProperty("ig.main.bot.password");

	public static final String FAKE_IG_USERNAME = System.getProperty("ig.fake.bot.username");
	public static final String FAKE_IG_PASSWORD = System.getProperty("ig.fake.bot.password");

	public static final Integer MAX_PHOTOS_FROM_TIMELINE_COUNT = Integer.parseInt(
			System.setProperty("ig.max.photos.from.timeline", System.getProperty("ig.max.photos.from.timeline", "20")));

}
