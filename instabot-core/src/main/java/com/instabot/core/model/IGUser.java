package com.instabot.core.model;

import com.instabot.core.client.instagram4j.Instagram4jIG;
import com.instabot.core.client.selenium.SeleniumIG;
import com.instabot.core.model.exception.CannotInitializeClientException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IGUser {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGUser.class);

	private String username;
	private String password;
	private UserType userType;

	private Instagram4j instagram4jIGClient;
	private RemoteWebDriver seleniumIGClient;

	public IGUser(String username, String password, UserType userType) {
		this.username = username;
		this.password = password;
		this.userType = userType;
	}

	public void login() {
		LOGGER.info("Login {} user {}", this.userType, this.username);
		loginDriver(DriverType.SELENIUM);
		loginDriver(DriverType.INSTAGRAM4J);
	}

	private void loginDriver(DriverType driver) {
		LOGGER.info("Initializing driver {} for user {}", driver, this.username);
		try {
			switch (driver) {
			case SELENIUM:
				seleniumIGClient = initSeleniumIGClient();
				break;
			case INSTAGRAM4J:
				instagram4jIGClient = initInstagram4jClientAndLogin();
				break;
			}
		} catch (CannotInitializeClientException e) {
			LOGGER.error("Cannot init driver {}", driver, e);
			throw new RuntimeException(e);
		}
	}

	// Register Selenium client for the user
	protected RemoteWebDriver initSeleniumIGClient() throws CannotInitializeClientException {
		return SeleniumIG.loginIG(this.username, this.password);
	}

	// Register Instagram4j client for the user
	protected Instagram4j initInstagram4jClientAndLogin() throws CannotInitializeClientException {
		return Instagram4jIG.loginIG(this.username, this.password);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public Instagram4j getInstagram4jIGClient() {
		return instagram4jIGClient;
	}

	public RemoteWebDriver getSeleniumIGClient() {
		return seleniumIGClient;
	}
}
