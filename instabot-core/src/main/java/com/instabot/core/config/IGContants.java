package com.instabot.core.config;

public final class IGContants {

	public static final String IG_BASE_URL = "https://www.instagram.com/";
	public static final String IG_LOGIN_URL = IG_BASE_URL + "accounts/login/";
	public static final String IG_PROFILE_URL = IG_BASE_URL + "%s";

	public static final String IG_USERNAME = System.getProperty("ig.bot.username");
	public static final String IG_PASSWORD = System.getProperty("ig.bot.password");

	private IGContants() {

	}
}
