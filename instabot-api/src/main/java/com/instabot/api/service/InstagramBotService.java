package com.instabot.api.service;

import com.instabot.api.model.entity.FollowedInfo;
import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.instabot.core.request.IGPhotosReq.TARGET_TYPE.HASHTAG;

@Service
public class InstagramBotService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramBotService.class);

	@Value("${ig.bot.api.max.follows.per.day:200}")
	private Integer MAX_FOLLOWS_PER_DAY;
	@Value("${ig.bot.api.max.likes.per.day:400}")
	private Integer MAX_LIKES_PER_DAY;

	@Value("${ig.bot.api.hashtag.photos.to.get:12}")
	private Integer HASHTAG_PHOTOS_TOGET;
	@Value("${ig.bot.api.hashtag.photos.to.return:3}")
	private Integer HASHTAG_PHOTOS_TORETURN;

	@Value("${ig.bot.api.unfollow.percentage:15}")
	private Integer PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED;
	@Value("${ig.bot.api.unfollow.older.than.days:3}")
	private Integer FOLLOWED_AT_LEAST_DAYS_BEFORE;

	@Value("${ig.bot.api.scheduled.request.min.hours.to.complete:15}")
	private Integer MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH;
	@Value("${ig.bot.api.scheduled.request.max.hours.to.complete:20}")
	private Integer MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH;

	@Autowired
	private InstagramFollowService instagramFollowService;

	@Autowired
	private InstagramLikeService instagramLikeService;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	@Autowired
	private UserRepository userRepository;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);

	private String mainUsername = mainIGUser.getUsername();

	public void cleanFollowingUsers() {
		LOGGER.info("Cleaning following users..");
		User dbUser = userRepository.findByUsername(mainUsername);

		List<FollowedInfo> currentlyFollowing = getCurrentlyFollowing(dbUser);

		currentlyFollowing =
				currentlyFollowing.stream()
						.sorted(Comparator.comparing(FollowedInfo::getDateFollowed))
						.limit(currentlyFollowing.size() / PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED)
						.collect(Collectors.toList());

		AtomicInteger unfollowedUsers = new AtomicInteger();

		currentlyFollowing.forEach(user -> {
			try {
				instagramFollowService.unfollow(user.getUsername());
				unfollowedUsers.getAndIncrement();

				// Retrieve the follower info for the user
				FollowedInfo currentFollowerInfo =
						dbUser.getEverFollowed().stream()
								.filter(x -> x.getUsername().equals(user.getUsername()))
								.findFirst().orElseThrow(RuntimeException::new);

				// remove it from the following list
				dbUser.getEverFollowed().remove(currentFollowerInfo);

				// change the status to not following
				currentFollowerInfo.setFollowStatus(FollowedInfo.FollowStatus.NOT_FOLLOWING);

				// add again in the collection but now its marked as not following
				dbUser.getEverFollowed().add(currentFollowerInfo);
				userRepository.saveAndFlush(dbUser);
			} catch (Exception e) {
				LOGGER.error("Cannot unfollow user:{}", user.getUsername(), e);
			}
		});

		LOGGER.info("Successfully unfollowed {} users", unfollowedUsers);
	}

	private List<FollowedInfo> getCurrentlyFollowing(User dbUser) {
		LocalDateTime now = LocalDateTime.now();

		return dbUser.getEverFollowed().stream()
				.filter(x -> x.getFollowStatus().equals(FollowedInfo.FollowStatus.FOLLOWING)
						&& x.getDateFollowed().plusDays(FOLLOWED_AT_LEAST_DAYS_BEFORE).isBefore(now))
				.collect(Collectors.toList());
	}

	public void followUsers() {
		LOGGER.info("Starting to following last {} users from the queue..", MAX_FOLLOWS_PER_DAY);
		User dbUser = userRepository.findByUsername(mainUsername);

		Collection<String> usersToFollow = getNElements(dbUser.getToFollow(), MAX_FOLLOWS_PER_DAY);

		AtomicInteger followedUsers = new AtomicInteger();

		new AutoSleepExecutor<>(usersToFollow, MAX_FOLLOWS_PER_DAY)
				.runTask((username) -> {
					LOGGER.info("Created follow request for user:{} ({}/{})",
							username, followedUsers.get() + 1, usersToFollow.size());
					instagramFollowService.follow(username);
					dbUser.getToFollow().remove(username);
					followedUsers.getAndIncrement();
					dbUser.getEverFollowed().add(new FollowedInfo(username));
					userRepository.saveAndFlush(dbUser);
				});

		LOGGER.info("Successfully followed {} users", followedUsers);
		LOGGER.info("Following users is done for today.");
	}

	private Collection<String> getNElements(Set<String> collection, int n) {
		return collection.stream().limit(n).collect(Collectors.toList());
	}

	public void likePhotos() {
		LOGGER.info("Starting to like last {} photos from the liking queue..", MAX_LIKES_PER_DAY);
		User dbUser = userRepository.findByUsername(mainUsername);

		Collection<String> photosToLike = getNElements(dbUser.getToLike(), MAX_LIKES_PER_DAY);

		AtomicInteger likedPhotos = new AtomicInteger();

		new AutoSleepExecutor<>(photosToLike, MAX_LIKES_PER_DAY)
				.runTask((photoId) -> {
					LOGGER.info("Created like request for photo:{} ({}/{})",
							photoId, likedPhotos.get() + 1, photosToLike.size());
					// First we can remove it because if the photo is deleted
					// we will get an exception, but we don't care, so will delete it first
					dbUser.getToLike().remove(photoId);
					userRepository.saveAndFlush(dbUser);
					instagramLikeService.likePhoto(photoId);
					likedPhotos.getAndIncrement();
				});

		LOGGER.info("Successfully liked {} photos", likedPhotos);
		LOGGER.info("Liking photos is done for today.");
	}

	public void addPhotosFromHashtag() {
		User dbUser = userRepository.findByUsername(mainUsername);
		Set<String> hashtags = dbUser.getHashtags();
		LOGGER.info("Starting to process hashtags {}", hashtags);

		for (String hashtag : hashtags) {
			List<String> currentHashtagPhotos =
					instagramPhotoService.getPhotos(HASHTAG, hashtag, HASHTAG_PHOTOS_TOGET, HASHTAG_PHOTOS_TORETURN, true);

			dbUser.getToLike().addAll(currentHashtagPhotos);
			LOGGER.info("Successfully added {} photos to like for hashtag:{}", currentHashtagPhotos.size(), hashtag);
		}

		userRepository.saveAndFlush(dbUser);
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
					LOGGER.error("Failed to execute the operation from AutoSleepExecutor for element:{} ,"
							+ " but will continue with the other elements", element, e);
				}

				long secondsToSleep = returnSleepInSeconds(executionsLimit);
				LOGGER.info("Sleeping for {} minutes", String.format("%.2g", secondsToSleep / 60.0));

				try {
					Thread.sleep(secondsToSleep * 1000);
				} catch (InterruptedException e) {
					LOGGER.error("Cannot sleep AutoSleepExecutor thread", e);
				}
			});
		}

		private long returnSleepInSeconds(int taskCount) {
			int minTimeToSleepInSeconds = (int) (((double) MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60);
			int maxTimeToSleepInSeconds = (int) (((double) MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60);
			return (long) Math
					.ceil(minTimeToSleepInSeconds + (Math.random() * (maxTimeToSleepInSeconds - minTimeToSleepInSeconds)));
		}
	}
}
