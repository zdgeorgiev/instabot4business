package com.instabot.core.util;

import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class IGUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGUtils.class);

	private IGUtils() {

	}

	public static Instagram4j login(String username, String password) {
		Instagram4j user = Instagram4j.builder()
				.username(username)
				.password(password)
				.build();

		user.setup();

		LOGGER.info("Loggin with username: {}, password {}",
				username, StringUtils.repeat("*", password.length()));

		try {
			user.login();
		} catch (IOException e) {
			LOGGER.error("Cannot login.", e);
			throw new RuntimeException("Cannot login.");
		}

		return user;
	}
}
