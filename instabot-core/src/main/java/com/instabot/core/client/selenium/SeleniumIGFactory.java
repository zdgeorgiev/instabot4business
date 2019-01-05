package com.instabot.core.client.selenium;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumIGFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumIGFactory.class);

	private static final String SELENIUM_DEBUG_MODE = System.setProperty("ig.selenium.debug",
			System.getProperty("ig.selenium.debug", "false"));

	private static RemoteWebDriver driver;

	public static RemoteWebDriver getClient() {
		if (driver == null) {
			LOGGER.debug("Initializing new Selenium web driver..");
			createWebDriver();
		}

		return driver;
	}

	private static void createWebDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox");

		chromeOptions.setHeadless(!Boolean.getBoolean(SELENIUM_DEBUG_MODE));

		final ChromeDriverService service = new ChromeDriverService.Builder()
				.usingAnyFreePort()
				.build();

		driver = new ChromeDriver(service, chromeOptions);
	}
}
