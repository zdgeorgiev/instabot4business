package com.instabot.api.pool;

import com.instabot.core.model.FakeIGUser;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.MainIGUser;
import com.instabot.core.model.UserType;

import java.util.ArrayList;
import java.util.List;

import static com.instabot.api.config.ConfigurationProperties.*;

public final class UsersPoolFactory {

	private static List<IGUser> users;

	static {
		users = new ArrayList<>();
		users.add(new MainIGUser(MAIN_IG_USERNAME, MAIN_IG_PASSWORD));
		users.add(new FakeIGUser(FAKE_IG_USERNAME, FAKE_IG_PASSWORD));
		users.forEach(IGUser::login);
	}

	public static IGUser getUser(UserType userType) {
		return userType == UserType.MAIN ? users.get(0) : users.get(1);
	}
}
