package com.instabot.api.service;

import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.filter.IGFilter;
import com.instabot.core.filter.NoPhotoUserFilter;
import com.instabot.core.filter.PrivateProfileFilter;
import com.instabot.core.filter.SpamCommentFilter;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGCommentsReq;
import com.instabot.core.request.IGFollowersReq;
import com.instabot.core.request.IGPhotosReq;
import com.instabot.core.strategy.UserSortingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.instabot.api.config.ConfigurationProperties.MAX_PHOTOS_FROM_TIMELINE_COUNT;

@Service
public class InstagramFollowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramFollowService.class);

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);
	private IGUser fakeIGUser = UsersPoolFactory.getUser(UserType.FAKE);

	public void follow(String username) {
		new IGFollowersReq(mainIGUser).followUser(username);
	}

	public List<String> getTopNotEverFollowedFollowers(String targetUsername,
			Class<? extends UserSortingStrategy> userSortingStrategy,
			int topUsersCount) {

		LOGGER.info("Collecting last {} photos of user {}", MAX_PHOTOS_FROM_TIMELINE_COUNT, targetUsername);
		List<String> mediaIds = new ArrayList<>(new IGPhotosReq(fakeIGUser)
				.getPhotosFor(IGPhotosReq.TARGET_TYPE.USER, targetUsername, MAX_PHOTOS_FROM_TIMELINE_COUNT));

		List<Class<? extends IGFilter>> filters = Arrays.asList(
				NoPhotoUserFilter.class,
				PrivateProfileFilter.class,
				SpamCommentFilter.class
		);

		Map<String, Integer> bestUsers = findTopNotEverFollowedFollowers(userSortingStrategy, mediaIds, filters);
		return sortTopFollowers(bestUsers, topUsersCount);
	}

	private Map<String, Integer> findTopNotEverFollowedFollowers(Class<? extends UserSortingStrategy> userSortingStrategy,
			List<String> mediaIds,
			List<Class<? extends IGFilter>> filters) {
		LOGGER.info("Finding top followers from the comments");
		return mediaIds.stream()
				.map(mediaId -> new IGCommentsReq(fakeIGUser)
						.applyFilters(filters)
						.applyUserSortingStrategy(userSortingStrategy)
						.getComments(mediaId))
				.reduce((a1, a2) -> {
							// If same user is present in couple of different medias
							// then combine the values of the comments
							a2.forEach((k, v) -> a1.merge(k, v, (v1, v2) -> v1 + v2));
							return a1;
						}
				).orElse(Collections.emptyMap());
	}

	private List<String> sortTopFollowers(Map<String, Integer> topFollowersScore, int topFollowersCount) {
		LOGGER.info("Sorting top followers list..");
		return topFollowersScore.entrySet().stream()
				// TODO: filter all users already followed at some time before
				//				.filter(follower -> !userRepository.get(mainIGUser.getUsername()).getAllTimeFollowed().contains(follower.getKey()))
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.limit(topFollowersCount)
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
