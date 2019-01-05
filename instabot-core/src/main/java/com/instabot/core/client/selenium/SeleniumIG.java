package com.instabot.core.client.selenium;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.instabot.core.config.IGContants.IG_LOGIN_URL;

public final class SeleniumIG {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumIG.class);

	private static RemoteWebDriver driver = SeleniumIGFactory.getClient();

	private SeleniumIG() {

	}

	public static RemoteWebDriver loginIG(String username, String password) {
		try {
			driver.get(IG_LOGIN_URL);

			driver.findElement(By.name("username")).sendKeys(username);
			driver.findElement(By.name("password")).sendKeys(password);
			driver.findElementByCssSelector("button[type=\"submit\"]").click();

			LOGGER.info("Login SeleniumIG client with username: {}, password {}",
					username, StringUtils.repeat("*", password.length()));

			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.warn("Error during loginIG", e);
		}

		return driver;
	}
}
