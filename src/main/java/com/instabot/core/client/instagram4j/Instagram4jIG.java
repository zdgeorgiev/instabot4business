package com.instabot.core.client.instagram4j;

import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.instabot.core.config.IGContants.IG_PASSWORD;
import static com.instabot.core.config.IGContants.IG_USERNAME;

public final class Instagram4jIG {

	private static final Logger LOGGER = LoggerFactory.getLogger(Instagram4jIG.class);

	private static Instagram4j userClient;

	private Instagram4jIG() {

	}

	public static Instagram4j getClient() {

		if (userClient != null) {
			LOGGER.debug("Returning already logged Instagram4j client..");
			return userClient;
		}

		userClient = Instagram4j.builder()
				.username(IG_USERNAME)
				.password(IG_PASSWORD)
				.build();

		userClient.setup();

		LOGGER.info("Login Instagram4j client with username: {}, password {}",
				IG_USERNAME, StringUtils.repeat("*", IG_PASSWORD.length()));

		try {
			userClient.login();
		} catch (IOException e) {
			LOGGER.error("Cannot getDriver.", e);
			throw new RuntimeException("Cannot getDriver.");
		}

		return userClient;
	}
}
