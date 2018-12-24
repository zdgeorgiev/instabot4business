package com.instabot.api.service;

import com.instabot.api.model.entity.FollowedInfo;
import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGPhotosReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstagramBotService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramBotService.class);

	private static final Integer MAX_FOLLOWS_PER_DAY = 120;
	private static final Integer MAX_LIKES_PER_DAY = 300;

	private static final Integer MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH = 15;
	private static final Integer MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH = 20;

	private static final Integer TOP_LIKERS_REQUEST_COUNT = 50;

	private static final Integer LAST_HASHTAG_PHOTOS_COUNT = 3;
	private static final Integer LAST_TOP_LIKERS_PHOTOS_COUNT = 8;

	private static final Integer TOP_LIKERS_PHOTOS_TO_BE_LIKED_COUNT = 2;

	private static final Integer PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED = 15;
	private static final Integer FOLLOWED_AT_LEAST_DAYS_BEFORE = 3;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);

	@Autowired
	private InstagramFollowService instagramFollowService;

	@Autowired
	private InstagramLikeService instagramLikeService;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	@Autowired
	private UserRepository userRepository;

	private String mainUsername = mainIGUser.getUsername();

	public void cleanFollowingUsers() {
		LOGGER.info("Cleaning following users..");
		User DBUser = userRepository.findByUsername(mainUsername);

		List<FollowedInfo> currentlyFollowing = getCurrentlyFollowing(DBUser);

		currentlyFollowing =
				currentlyFollowing.stream()
						.limit(currentlyFollowing.size() / PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED)
						.collect(Collectors.toList());

		currentlyFollowing.forEach(user -> {
			try {
				instagramFollowService.unfollow(user.getUsername());

				// Retrieve the follower info
				FollowedInfo currentFollowerInfo =
						DBUser.getEverFollowed().stream()
								.filter(x -> x.getUsername().equals(user.getUsername()))
								.findFirst().orElseThrow(RuntimeException::new);

				// remove it from the following list
				DBUser.getEverFollowed().remove(currentFollowerInfo);

				// change the status to not following anymore
				currentFollowerInfo.setFollowStatus(FollowedInfo.FollowStatus.NOT_FOLLOWING);

				// add again in the collection but now its marked as not following anymore
				DBUser.getEverFollowed().add(currentFollowerInfo);
				userRepository.saveAndFlush(DBUser);
			} catch (Exception e) {
				LOGGER.error("Cannot unfollow user:{}", user.getUsername(), e);
			}
		});
	}

	private List<FollowedInfo> getCurrentlyFollowing(User DBUser) {
		LocalDateTime now = LocalDateTime.now();

		// TODO:TEST IF THE SORTING IS WORKING
		return DBUser.getEverFollowed().stream()
				.filter(x -> x.getFollowStatus().equals(FollowedInfo.FollowStatus.FOLLOWING)
						&& x.getDateFollowed().plusDays(FOLLOWED_AT_LEAST_DAYS_BEFORE).isBefore(now))
				.sorted((o1, o2) -> o2.getDateFollowed().compareTo(o1.getDateFollowed()))
				.collect(Collectors.toList());
	}

	public void followUsers() {
		LOGGER.info("Starting to following last {} users from the queue..", MAX_FOLLOWS_PER_DAY);
		User DBUser = userRepository.findByUsername(mainUsername);

		Collection<String> usersToFollow = getNElements(DBUser.getToFollow(), MAX_FOLLOWS_PER_DAY);
		DBUser.getToFollow().removeAll(usersToFollow);
		userRepository.saveAndFlush(DBUser);

		new AutoSleepExecutor<>(usersToFollow, MAX_FOLLOWS_PER_DAY)
				.runTask((username) -> {
					LOGGER.info("Created follow request for user:{}..", username);

					if (DBUser.getEverFollowed().stream().noneMatch(x -> x.getUsername().equals(username))) {
						instagramFollowService.follow(username);
						DBUser.getEverFollowed().add(new FollowedInfo(username));
					} else {
						LOGGER.info("User:{} already followed before", username);
					}
				});

		userRepository.saveAndFlush(DBUser);
		LOGGER.info("Following users is done for today.");
	}

	private Collection<String> getNElements(Set<String> collection, int n) {
		return collection.stream().limit(n).collect(Collectors.toList());
	}

	public void likePhotos() {
		LOGGER.info("Starting to like last {} photos from the liking queue..", MAX_LIKES_PER_DAY);
		User DBUser = userRepository.findByUsername(mainUsername);

		Collection<String> photosToLike = getNElements(DBUser.getToLike(), MAX_LIKES_PER_DAY);
		DBUser.getToLike().removeAll(photosToLike);
		userRepository.saveAndFlush(DBUser);

		new AutoSleepExecutor<>(photosToLike, MAX_LIKES_PER_DAY)
				.runTask((photoId) -> {
					LOGGER.info("Created like request for photo:{}..", photoId);
					instagramLikeService.likePhoto(photoId);
				});

		LOGGER.info("Liking photos is done for today.");
	}

	private interface Executor<T> {
		void execute(T element) throws Exception;
	}

	private class AutoSleepExecutor<T> {

		private final Logger LOGGER = LoggerFactory.getLogger(AutoSleepExecutor.class);

		private Collection<T> collection;
		private int executionsLimit;

		AutoSleepExecutor(Collection<T> collection, int executionsLimit) {
			this.collection = collection;
			this.executionsLimit = executionsLimit;
		}

		void runTask(Executor<T> executor) {
			collection.forEach(element -> {

				try {
					executor.execute(element);
				} catch (Exception e) {
					LOGGER.error("Failed to execute the operation from AutoSleepExecutor for element {} ,"
							+ " but will continue with the other elements", element, e);
				}

				double secondsToSleep = returnSleepInSeconds(executionsLimit);
				LOGGER.info("Sleeping for {} minutes", String.format("%.2g", secondsToSleep / 60));

				try {
					Thread.sleep((long) secondsToSleep * 1000);
				} catch (InterruptedException e) {
					LOGGER.error("Cannot sleep AutoSleepExecutor thread", e);
				}
			});
		}

		private long returnSleepInSeconds(int taskCount) {
			int minTimeToSleepInSeconds = (MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60;
			int maxTimeToSleepInSeconds = (MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60;
			return (long) Math
					.ceil(minTimeToSleepInSeconds + (Math.random() * (maxTimeToSleepInSeconds - minTimeToSleepInSeconds)));
		}
	}

	public void addPhotosFromHashtag() {
		User DBUser = userRepository.findByUsername(mainUsername);
		Set<String> hashtags = DBUser.getHashtags();
		LOGGER.info("Starting to process hashtags {}", hashtags);

		for (String hashtag : hashtags) {
			List<String> currentHashtagPhotos =
					instagramPhotoService.getPhotos(IGPhotosReq.TARGET_TYPE.HASHTAG, hashtag, LAST_HASHTAG_PHOTOS_COUNT);

			DBUser.getToLike().addAll(currentHashtagPhotos);
			userRepository.saveAndFlush(DBUser);
			LOGGER.info("Successfully added {} photos to like for hashtag:", currentHashtagPhotos.size(), hashtag);
		}
	}
}
