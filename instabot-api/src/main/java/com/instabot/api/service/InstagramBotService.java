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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstagramBotService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramBotService.class);

	private static final Integer MAX_FOLLOWS_PER_DAY = 120;
	private static final Integer MAX_LIKES_PER_DAY = 200;

	private static final Integer MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH = 15;
	private static final Integer MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH = 20;

	private static final Integer TOP_LIKERS_REQUEST_COUNT = 50;

	private static final Integer LAST_HASHTAG_PHOTOS_COUNT = 3;
	private static final Integer LAST_TOP_LIKERS_PHOTOS_COUNT = 8;

	private static final Integer TOP_LIKERS_PHOTOS_TO_BE_LIKED_COUNT = 2;

	private static final Integer PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED = 15;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);

	@Autowired
	private InstagramFollowService instagramFollowService;

	@Autowired
	private InstagramLikeService instagramLikeService;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	@Autowired
	private UserRepository userRepository;

	private String mainUsername;

	@PostConstruct
	public void initDBUser() {
		this.mainUsername = mainIGUser.getUsername();
	}

	public void cleanFollowingUsers() {
		LOGGER.info("Cleaning following users..");
		// get all current following users that they are not following us
		// find them in all time followed users and see when they are followed
		// unfollow PERCENTAGE_OF_TOTAL_FOLLOWINGS_TO_BE_UNFOLLOWED of total followings
		// TODO: implement cleaning process for users that we follow
		throw new NotImplementedException();
	}

	public void followUsers() {
		LOGGER.info("Starting to following last {} users from the queue..", MAX_FOLLOWS_PER_DAY);
		User DBUser = userRepository.findByUsername(mainUsername);

		Collection<String> usersToFollow = getNElements(DBUser.getToFollow(), MAX_FOLLOWS_PER_DAY);
		DBUser.getToFollow().removeAll(usersToFollow);
		userRepository.saveAndFlush(DBUser);

		for (String username : usersToFollow) {
			new AutoSleepExecutor(MAX_FOLLOWS_PER_DAY).runTask(() -> {
				LOGGER.info("Created follow request for user:{}..", username);
				instagramFollowService.follow(username);
				DBUser.getEverFollowed().add(new FollowedInfo(username));
			});
		}

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

		for (String photoId : photosToLike) {
			new AutoSleepExecutor(MAX_LIKES_PER_DAY).runTask(() -> {
				LOGGER.info("Created like request for photo:{}..", photoId);
				instagramLikeService.likePhoto(photoId);
			});
		}

		LOGGER.info("Liking photos is done for today.");
	}

	private interface Execution {
		void execute();
	}

	private class AutoSleepExecutor {

		private int tasksCount;

		public AutoSleepExecutor(int task) {
			this.tasksCount = task;
		}

		public void runTask(Execution execution) {
			execution.execute();
			double secondsToSleep = returnSleepInSeconds(tasksCount);
			LOGGER.info("Sleeping for {} minutes", String.format("%.2g", secondsToSleep / 60));

			try {
				Thread.sleep((long) secondsToSleep * 1000);
			} catch (InterruptedException e) {
				LOGGER.error("Cannot sleep executor thread", e);
			}
		}

		private long returnSleepInSeconds(int taskCount) {
			double minTimeToSleepInSeconds = ((double)MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60;
			double maxTimeToSleepInSeconds = ((double)MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH / taskCount) * 60 * 60;
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
