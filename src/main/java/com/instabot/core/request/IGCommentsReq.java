package com.instabot.core.request;

import com.instabot.core.filter.CommentFilter;
import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.UserFilter;
import org.apache.commons.lang3.StringUtils;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetMediaCommentsRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetMediaCommentsResult;
import org.brunocvcunha.instagram4j.requests.payload.InstagramUser;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class IGCommentsReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGCommentsReq.class);

	private static final int TIMEOUT_SLEEP_SECONDS = 30;
	private static final int MAX_USER_COMMENTS_ON_PHOTO = 3;

	private final Map<String, List<String>> userComments;
	private Instagram4j executor;
	private List<IGFilter> filters;
	private String mediaId;
	private String nextMediaPage;

	public IGCommentsReq(Instagram4j executor, String mediaCode) {
		this.userComments = new HashMap<>();
		this.executor = executor;
		this.mediaId = InstagramCodeUtil.fromCode(mediaCode) + "";
	}

	public Map<String, List<String>> execute() {

		LOGGER.info("Starting to collect comments for media: {}", mediaId);

		try {
			executeRequest();
		} catch (InterruptedException e) {
			LOGGER.error("Thread cannot sleep", e);
		}

		LOGGER.info("Successfully collected {} comments", userComments.size());
		return Collections.unmodifiableMap(userComments);
	}

	@SafeVarargs
	public final IGCommentsReq applyFilters(Class<? extends IGFilter>... filters) {
		this.filters = initializeFilters(Arrays.stream(filters).collect(Collectors.toList()));
		return this;
	}

	private void executeRequest() throws InterruptedException {

		LOGGER.info("Collected {} comments", userComments.size());

		try {
			while (true) {
				InstagramGetMediaCommentsResult commentsResult =
						executor.sendRequest(new InstagramGetMediaCommentsRequest(mediaId, nextMediaPage));

				commentsResult.getComments()
						.forEach(comment -> {
							InstagramUser user = comment.getUser();
							String commentText = comment.getText();

							for (IGFilter filter : filters) {
								if (filter instanceof CommentFilter)
									commentText = (String) filter.apply(commentText);
								else if (filter instanceof UserFilter) {
									user = (InstagramUser) filter.apply(user);
								} else {
									throw new UnsupportedOperationException("Not supporting " + filter.getClass().getName());
								}
							}

							if (isUserValid(user) && isCommentValid(commentText)) {
								if (!userComments.containsKey(user.username))
									userComments.put(user.username, new ArrayList<>());

								userComments.get(user.username).add(commentText);
							}
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
			executeRequest();
		}
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
}
