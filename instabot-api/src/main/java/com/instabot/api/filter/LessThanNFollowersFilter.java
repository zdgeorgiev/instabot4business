package com.instabot.api.filter;

import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.filter.UserFilter;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;

import static com.instabot.core.config.IGContants.IG_PROFILE_URL;

public class LessThanNFollowersFilter implements UserFilter {

	private static final int FOLLOWERS_LIMIT = 1500;

	private IGUser fakeUser = UsersPoolFactory.getUser(UserType.FAKE);

	@Override
	public boolean apply(InstagramUser user) {
		fakeUser.getSeleniumIGClient().get(String.format(IG_PROFILE_URL, user.getUsername()));
		String followers = fakeUser.getSeleniumIGClient().findElementsByClassName("g47SY ").get(1).getText();
		return !followers.contains("k") && !followers.contains("m") && Integer.parseInt(followers) < FOLLOWERS_LIMIT;
	}
}
