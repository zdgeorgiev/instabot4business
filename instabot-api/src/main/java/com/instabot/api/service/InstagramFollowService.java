package com.instabot.api.service;

import com.instabot.api.config.FiltersConfig;
import com.instabot.api.model.UserSortingStrategyType;
import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGCommentsReq;
import com.instabot.core.request.IGFollowersReq;
import com.instabot.core.strategy.UserSortingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.instabot.core.request.IGPhotosReq.TARGET_TYPE.USER;

@Service
public class InstagramFollowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramFollowService.class);

	@Value("${ig.bot.api.last.user.photos.to.get:30}")
	private Integer USER_PHOTOS_TOGET;

	@Value("${ig.bot.api.top.followers.percentage.to.follow:75}")
	private Integer TOP_FOLLOWERS_PERCENTAGE_TOFOLLOW;

	@Value("${ig.bot.api.top.followers.photos.to.get:3}")
	private Integer TOP_FOLLOWERS_PHOTOS_TOGET;
	@Value("${ig.bot.api.top.followers.photos.to.return:1}")
	private Integer TOP_FOLLOWERS_PHOTOS_TORETURN;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);
	private IGUser fakeIGUser = UsersPoolFactory.getUser(UserType.FAKE);

	private String mainUsername = mainIGUser.getUsername();

	public void follow(String username) {
		try {
			new IGFollowersReq(mainIGUser).followUser(username);
			LOGGER.info("{} user:{} followed {}", mainIGUser.getUserType(), mainIGUser.getUsername(), username);
		} catch (Exception e) {
			LOGGER.error("Cannot follow user {}", username, e);
			throw new RuntimeException(e);
		}
	}

	public void unfollow(String username) {
		try {
			new IGFollowersReq(mainIGUser).unfollow(username);
			LOGGER.info("Successfully unfollowed user:{}", username);
		} catch (Exception e) {
			LOGGER.error("Cannot unfollow user:{}", username, e);
			throw new RuntimeException(e);
		}
	}

	public void processTopFollowers(String username, UserSortingStrategyType userSortingStrategy, int limit) {
		LOGGER.info("Starting to process top {} followers for users:{}", limit, username);

		User dbUser = userRepository.findByUsername(mainUsername);
		Set<String> usersBlacklist = new HashSet<>();
		usersBlacklist.add(username);
		usersBlacklist.add(mainUsername);

		List<String> userPhotoIds = instagramPhotoService
				.getPhotos(USER, username, USER_PHOTOS_TOGET, USER_PHOTOS_TOGET, false);

		List<String> topNotEverFollowedFollowers =
				getTopNotEverFollowedFollowers(userPhotoIds, userSortingStrategy.getStrategyClass(), usersBlacklist, limit);

		int usersToFollow = (int) (topNotEverFollowedFollowers.size() * (TOP_FOLLOWERS_PERCENTAGE_TOFOLLOW / 100.0));
		int usersToLike = topNotEverFollowedFollowers.size() - usersToFollow;

		List<String> toFollow = topNotEverFollowedFollowers.subList(0, usersToFollow);
		List<String> toLike = topNotEverFollowedFollowers.subList(usersToFollow, usersToFollow + usersToLike);

		dbUser.getToFollow().addAll(toFollow);

		int photosAdded = 0;
		for (int i = 0; i < toLike.size(); i++) {
			String user = toLike.get(i);
			LOGGER.info("Processing user {}: ({}/{})", user, i + 1, toLike.size());
			List<String> userPhotosToLIke = instagramPhotoService
					.getPhotos(USER, user, TOP_FOLLOWERS_PHOTOS_TOGET, TOP_FOLLOWERS_PHOTOS_TORETURN, true);
			dbUser.getToLike().addAll(userPhotosToLIke);
			photosAdded += userPhotosToLIke.size();
		}

		userRepository.flush();
		LOGGER.info("Added {} new users to follow from top followers for user:{}", toFollow.size(), username);
		LOGGER.info("Added {} new photos to like from top followers for user:{}", photosAdded, username);
	}

	private List<String> getTopNotEverFollowedFollowers(List<String> mediaIds,
			Class<? extends UserSortingStrategy> userSortingStrategy, Set<String> usersBlacklist, int limit) {

		return toSortedList(findTopNotEverFollowedFollowers(mediaIds, userSortingStrategy), usersBlacklist, limit);
	}

	private Map<String, Integer> findTopNotEverFollowedFollowers(List<String> mediaIds,
			Class<? extends UserSortingStrategy> userSortingStrategy) {

		LOGGER.info("Finding top followers from the comments");

		return mediaIds.stream()
				.map(mediaId -> new IGCommentsReq(fakeIGUser)
						.applyFilters(FiltersConfig.getFilters())
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

	private List<String> toSortedList(Map<String, Integer> topFollowersScore, Set<String> usersBlacklist, int limit) {
		LOGGER.info("Sorting top followers list..");
		return topFollowersScore.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey)
				.filter(this::neverFollowed)
				.filter(x -> !usersBlacklist.contains(x))
				.limit(limit)
				.collect(Collectors.toList());
	}

	private boolean neverFollowed(String username) {
		return userRepository.findByUsername(mainIGUser.getUsername())
				.getEverFollowed().stream()
				.noneMatch(x -> x.getUsername().equals(username));
	}
}
