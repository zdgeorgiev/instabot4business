package com.instabot.api.config;

import com.instabot.api.filter.*;
import com.instabot.core.filter.IGFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class FiltersConfig {

	@Value("${ig.bot.api.filter.skip.users.with.more.than.followers:true}")
	private Boolean USERS_WITH_MORE_THAN_FOLLOWERS_FILTER;
	@Value("${ig.bot.api.filter.skip.private.accounts:true}")
	private Boolean USERS_WITHOUT_PRIVATE_ACCOUNTS;
	@Value("${ig.bot.api.filter.skip.business.accounts:true}")
	private Boolean USERS_NOT_BUSINESS_ACCOUNTS;
	@Value("${ig.bot.api.filter.skip.users.without.profile.picture:true}")
	private Boolean USERS_WITHOUT_PROFILE_PICTURE;
	@Value("${ig.bot.api.filter.skip.spam.comments:true}")
	private Boolean WITHOUT_SPAM_COMMENTS;

	private static List<Class<? extends IGFilter>> enabledFilters = new ArrayList<>();

	@PostConstruct
	public void init() {
		if (USERS_WITH_MORE_THAN_FOLLOWERS_FILTER)
			enabledFilters.add(LessThanNFollowersFilter.class);
		if (USERS_WITHOUT_PRIVATE_ACCOUNTS)
			enabledFilters.add(PublicProfileFilter.class);
		if (USERS_NOT_BUSINESS_ACCOUNTS)
			enabledFilters.add(NotABusinessAccount.class);
		if (USERS_WITHOUT_PROFILE_PICTURE)
			enabledFilters.add(UserWithProfilePictureFilter.class);
		if (WITHOUT_SPAM_COMMENTS)
			enabledFilters.add(NotASpamCommentFilter.class);
	}

	public static List<Class<? extends IGFilter>> getFilters() {
		return enabledFilters;
	}
}
