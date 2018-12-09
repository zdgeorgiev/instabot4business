package com.instabot.core.client.selenium;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.instabot.core.config.SeleniumConstants.SELENIUM_CHROME_DRIVER_PATH;
import static com.instabot.core.config.SeleniumConstants.SELENIUM_DEBUG_MODE;

public class SeleniumFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumFactory.class);

	static {
		System.setProperty(SELENIUM_CHROME_DRIVER_PATH,
				System.getProperty(SELENIUM_CHROME_DRIVER_PATH, "src/main/resources/chromedriver.exe"));

		System.setProperty(SELENIUM_DEBUG_MODE,
				System.getProperty(SELENIUM_DEBUG_MODE, "false"));
	}

	private static RemoteWebDriver driver;

	public static RemoteWebDriver getDriver() {
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
