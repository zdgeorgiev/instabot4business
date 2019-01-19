package com.instabot.api.service;

import com.instabot.api.model.entity.FollowedInfo;
import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGUploadPhotoReq;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
	@Value("${ig.bot.api.max.unfollows.per.day:250}")
	private Integer MAX_UNFOLLOWS_PER_DAY;

	@Value("${ig.bot.api.unfollow.older.than.days:0}")
	private Integer FOLLOWED_AT_LEAST_DAYS_BEFORE;

	@Value("${ig.bot.api.hashtag.photos.to.get:12}")
	private Integer HASHTAG_PHOTOS_TOGET;
	@Value("${ig.bot.api.hashtag.photos.to.return:3}")
	private Integer HASHTAG_PHOTOS_TORETURN;

	@Value("${ig.bot.api.scheduled.request.min.hours.to.complete:15}")
	private Integer MIN_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH;
	@Value("${ig.bot.api.scheduled.request.max.hours.to.complete:20}")
	private Integer MAX_HOURS_FOR_SCHEDULED_REQUEST_TO_FINISH;

	@Value("${ig.bot.api.photos.to.upload:2}")
	private Integer PHOTOS_TO_UPLOAD;
	@Value("${ig.bot.api.photos.dir.path:}")
	private String PHOTOS_DIR_PATH;
	@Value("${ig.bot.api.photos.sleep.between.each.upload:60}")
	private Integer PHOTOS_WAIT_BEFORE_UPLOAD_NEXT_MINTUES;

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

		Collection<FollowedInfo> usersToUnfollow = getNElements(getUnfollowCandidates(dbUser), MAX_UNFOLLOWS_PER_DAY);

		AtomicInteger unfollowedUsers = new AtomicInteger();

		new AutoSleepExecutor<>(usersToUnfollow, MAX_UNFOLLOWS_PER_DAY)
				.runTask(followedInfo -> {
					String username = followedInfo.getUsername();
					LOGGER.info("Created unfollow request for user:{} ({}/{})",
							username, unfollowedUsers.incrementAndGet(), usersToUnfollow.size());

					try {
						instagramFollowService.unfollow(username);
						dbUser.getEverFollowed().stream()
								.filter(x -> x.getUsername().equals(followedInfo.getUsername()))
								.findFirst().get()
								.setFollowStatus(FollowedInfo.FollowStatus.NOT_FOLLOWING);
					} catch (Exception e) {
						LOGGER.error("Cannot unfollow user:{}", username, e);
					} finally {
						userRepository.saveAndFlush(dbUser);
					}
				});

		LOGGER.info("Unfollowing users is done for today.");
	}

	private List<FollowedInfo> getUnfollowCandidates(User dbUser) {
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
							username, followedUsers.incrementAndGet(), usersToFollow.size());
					try {
						dbUser.getToFollow().remove(username);
						instagramFollowService.follow(username);
						dbUser.getEverFollowed().add(new FollowedInfo(username, FollowedInfo.FollowStatus.FOLLOWING));
					} finally {
						userRepository.saveAndFlush(dbUser);
					}
				});

		LOGGER.info("Following users is done for today.");
	}

	private <T> Collection<T> getNElements(Collection<T> collection, int n) {
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
							photoId, likedPhotos.incrementAndGet(), photosToLike.size());
					try {
						dbUser.getToLike().remove(photoId);
						instagramLikeService.likePhoto(photoId);
					} finally {
						userRepository.saveAndFlush(dbUser);
					}
				});

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

	public void uploadPhotos() {
		LOGGER.info("Starting to upload photos..");

		File picturesDir = new File(PHOTOS_DIR_PATH);

		if (!picturesDir.exists() || !picturesDir.isDirectory() || picturesDir.listFiles() == null)
			return;

		List<File> allPhotos = Arrays.stream(
				Objects.requireNonNull(picturesDir.listFiles((dir, name) -> name.contains(".jpg") || name.contains(".png"))))
				.collect(Collectors.toList());

		CopyOnWriteArrayList<File> photosToUpload = new CopyOnWriteArrayList<>();
		photosToUpload.addAll(allPhotos.subList(0, Math.min(allPhotos.size(), PHOTOS_TO_UPLOAD)));

		int uploadedPhotosCount = 0;

		for (File photo : photosToUpload) {
			try {
				String description = getDescriptionIfAvailable(photo.getName());
				new IGUploadPhotoReq(mainIGUser).uploadPhoto(photo, description);
				LOGGER.info("Successfully upload photo ({}/{}) -> {}",
						++uploadedPhotosCount, photosToUpload.size(), photo.getName());
				Thread.sleep(PHOTOS_WAIT_BEFORE_UPLOAD_NEXT_MINTUES * 1000 * 60);
			} catch (Exception e) {
				LOGGER.error("Cannot upload photo:{}", photo.getAbsolutePath(), e);
			} finally {
				photo.delete();
			}
		}

		LOGGER.info("Finished uploading photos for today.");
	}

	private String getDescriptionIfAvailable(String photoName) throws IOException {
		File descriptionFile = new File(PHOTOS_DIR_PATH, photoName.substring(0, photoName.indexOf(".")) + ".txt");
		String content = descriptionFile.exists() ? FileUtils.readFileToString(descriptionFile) : "";

		if (descriptionFile.exists())
			descriptionFile.delete();

		return content;
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
