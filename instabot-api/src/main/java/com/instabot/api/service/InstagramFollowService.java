package com.instabot.api.service;

import com.instabot.api.model.UserSortingStrategyType;
import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InstagramFollowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramFollowService.class);

	private static final Integer TOP_FOLLOWERS_REQUEST_COUNT = 100;

	private static final Integer LAST_USER_PHOTOS_COUNT = 30;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	@Autowired
	private InstagramFollowService instagramFollowService;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);
	private IGUser fakeIGUser = UsersPoolFactory.getUser(UserType.FAKE);

	private String mainUsername;

	@PostConstruct
	public void initDBUser() {
		this.mainUsername = mainIGUser.getUsername();
	}

	public void follow(String username) {
		new IGFollowersReq(mainIGUser).followUser(username);
	}

	public void addTopTargetFollowers(String username, UserSortingStrategyType userSortingStrategy) {

		User DBUser = userRepository.findByUsername(mainUsername);

		List<String> userPhotoIds = instagramPhotoService
				.getPhotos(IGPhotosReq.TARGET_TYPE.USER, username, LAST_USER_PHOTOS_COUNT);

		List<String> topNotEverFollowedFollowers = instagramFollowService
				.getTopNotEverFollowedFollowers(userPhotoIds, userSortingStrategy.getStrategyClass(), TOP_FOLLOWERS_REQUEST_COUNT);

		DBUser.getToFollow().addAll(topNotEverFollowedFollowers);
		userRepository.saveAndFlush(DBUser);
		LOGGER.info("Added {} new users to follow from user:{}", topNotEverFollowedFollowers.size(), username);
	}

	private List<String> getTopNotEverFollowedFollowers(List<String> mediaIds,
			Class<? extends UserSortingStrategy> userSortingStrategy,
			int topFollowersCount) {

		Map<String, Integer> topFollowers = findTopNotEverFollowedFollowers(mediaIds, userSortingStrategy);
		return sortTopFollowers(topFollowers, topFollowersCount);
	}

	private Map<String, Integer> findTopNotEverFollowedFollowers(List<String> mediaIds,
			Class<? extends UserSortingStrategy> userSortingStrategy) {

		List<Class<? extends IGFilter>> filters = Arrays.asList(
				NoPhotoUserFilter.class,
				PrivateProfileFilter.class,
				SpamCommentFilter.class
		);

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
				.filter(follower ->
						userRepository.findByUsername(mainIGUser.getUsername())
								.getEverFollowed().stream()
								.noneMatch(x -> x.getUsername().equals(follower.getKey())))
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.limit(topFollowersCount)
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey).collect(Collectors.toList());
	}
}
