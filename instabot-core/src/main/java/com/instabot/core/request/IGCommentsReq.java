package com.instabot.core.request;

import com.instabot.core.collections.UserCommentsCollection;
import com.instabot.core.filter.CommentFilter;
import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.UserFilter;
import com.instabot.core.model.IGUser;
import com.instabot.core.strategy.UserSortingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.requests.InstagramGetMediaCommentsRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetMediaCommentsResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IGCommentsReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGCommentsReq.class);

	private static final int TIMEOUT_SLEEP_SECONDS = 30;
	private static final int MAX_USER_COMMENTS_ON_PHOTO = 5;

	private UserCommentsCollection userComments;
	private IGUser user;
	private List<IGFilter> filters;
	private String nextMediaPage;

	public IGCommentsReq(IGUser user) {
		this.user = user;
		this.userComments = new UserCommentsCollection();
	}

	public Map<String, Integer> getComments(String mediaId) {

		String mediaCode = InstagramCodeUtil.fromCode(mediaId) + "";
		LOGGER.info("Starting to collect comments for media: {}", mediaCode);

		try {
			executeRequest(mediaCode);
		} catch (InterruptedException e) {
			LOGGER.error("Thread cannot sleep", e);
		}

		cleanSpammyUsers();

		LOGGER.info("Successfully collected {} comments", userComments.size());
		return userComments.normalize();
	}

	private void executeRequest(String mediaCode) throws InterruptedException {

		try {
			while (true) {
				InstagramGetMediaCommentsResult commentsResult =
						user.getInstagram4jIGClient().sendRequest(new InstagramGetMediaCommentsRequest(mediaCode, nextMediaPage));

				commentsResult.getComments()
						.forEach(comment -> {
							InstagramUser user = comment.getUser();
							String commentText = comment.getText();

							for (IGFilter<?> filter : filters) {
								if (filter instanceof CommentFilter)
									commentText = ((CommentFilter) filter).apply(commentText);
								else if (filter instanceof UserFilter) {
									user = ((UserFilter) filter).apply(user);
								} else {
									throw new UnsupportedOperationException("Not supporting " + filter.getClass().getName());
								}
							}

							if (isUserValid(user) && isCommentValid(commentText))
								userComments.addComment(user.username, commentText);
						});

				nextMediaPage = commentsResult.getNext_max_id();
				if (nextMediaPage == null) {
					break;
				}
				nextMediaPage = URLEncoder.encode(nextMediaPage, "UTF-8");
			}
		} catch (Exception e) {
			LOGGER.info("Sleeping for {}s because of too much requests.", TIMEOUT_SLEEP_SECONDS, e);
			Thread.sleep(TIMEOUT_SLEEP_SECONDS * 1000);
			executeRequest(mediaCode);
		}
	}

	public final IGCommentsReq applyFilters(List<Class<? extends IGFilter>> filters) {
		this.filters = initializeFilters(filters);
		return this;
	}

	private boolean isCommentValid(String comment) {
		return !StringUtils.isEmpty(comment);
	}

	private boolean isUserValid(InstagramUser user) {
		return user != null;
	}

	private List<IGFilter> initializeFilters(List<Class<? extends IGFilter>> filters) {
		return filters.stream().map(filter -> {
			try {
				LOGGER.info("Adding {}", filter.getName());
				return filter.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Cannot create filter " + filter.getName(), e);
			}
		}).collect(Collectors.toList());
	}

	private void cleanSpammyUsers() {
		LOGGER.debug("Starting to remove users with more than {} comments", MAX_USER_COMMENTS_ON_PHOTO);

		List<String> blackListedUsers =
				userComments.entrySet().stream()
						.filter(user -> user.getValue().size() > MAX_USER_COMMENTS_ON_PHOTO)
						.map(Map.Entry::getKey)
						.collect(Collectors.toList());

		blackListedUsers.forEach(userComments::remove);
		LOGGER.debug("Successfully removed {} users", blackListedUsers.size());
	}

	public IGCommentsReq applyUserSortingStrategy(Class<? extends UserSortingStrategy> strategy) {
		this.userComments.setSortStrategy(strategy);
		return this;
	}
}
