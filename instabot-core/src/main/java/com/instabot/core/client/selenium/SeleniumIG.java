package com.instabot.core.client.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.instabot.core.config.IGContants.*;

public final class SeleniumIG {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumIG.class);

	private static RemoteWebDriver driver;

	private SeleniumIG() {

	}

	public static RemoteWebDriver getClient() {

		driver = SeleniumFactory.getDriver();

		login();

		return driver;
	}

	private static void login() {
		try {
			driver.get(IG_LOGIN_URL);

			driver.findElement(By.name("username")).sendKeys(IG_USERNAME);
			driver.findElement(By.name("password")).sendKeys(IG_PASSWORD);
			driver.findElementByCssSelector("button[type=\"submit\"]").click();

			Thread.sleep(2000);
		} catch (Exception e) {
			LOGGER.warn("Error during login", e);
		}
	}
}
