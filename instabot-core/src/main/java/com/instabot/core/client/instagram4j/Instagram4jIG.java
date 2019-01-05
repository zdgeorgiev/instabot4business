package com.instabot.core.client.instagram4j;

import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class Instagram4jIG {

	private static final Logger LOGGER = LoggerFactory.getLogger(Instagram4jIG.class);

	private static Instagram4j userClient;

	private Instagram4jIG() {

	}

	public static Instagram4j loginIG(String username, String password) {

		if (userClient == null) {
			userClient = Instagram4j.builder()
					.username(username)
					.password(password)
					.build();

			userClient.setup();
		}

		LOGGER.info("Login Instagram4j client with username: {}, password {}",
				username, StringUtils.repeat("*", password.length()));

		try {
			userClient.login();
		} catch (IOException e) {
			LOGGER.error("Cannot getClient.", e);
			throw new RuntimeException("Cannot getClient.");
		}

		return userClient;
	}
}
