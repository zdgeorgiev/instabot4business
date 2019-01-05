package com.instabot.api.service;

import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGLikesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.instabot.core.request.IGPhotosReq.TARGET_TYPE.USER;

@Service
public class InstagramLikeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramLikeService.class);

	private static final Integer TOP_LIKERS_PERCENTAGE_TOFOLLOW = 10;

	private static final Integer USER_PHOTOS_TOGET = 30;

	private static final Integer TOP_LIKERS_PHOTOS_TOGET = 3;
	private static final Integer TOP_LIKERS_PHOTOS_TORETURN = 1;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	private String mainUsername = UsersPoolFactory.getUser(UserType.MAIN).getUsername();

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);
	private IGUser fakeIGUser = UsersPoolFactory.getUser(UserType.FAKE);

	public void likePhoto(String mediaId) {
		try {
			new IGLikesRequest(mainIGUser).likePhoto(mediaId);
			LOGGER.info("{} user:{} liked photo /p/{}", mainIGUser.getUserType(), mainIGUser.getUsername(), mediaId);
		} catch (IOException e) {
			LOGGER.error("Cannot like photo {}", mediaId, e);
			throw new RuntimeException(e);
		}
	}

	public void processTopLikers(String username, int limit) {
		LOGGER.info("Starting to process top {} likers for users:{}", limit, username);

		User dbUser = userRepository.findByUsername(mainUsername);

		List<String> userPhotoIds = instagramPhotoService
				.getPhotos(USER, username, USER_PHOTOS_TOGET, USER_PHOTOS_TOGET, false);

		List<String> topLikers = toSortedList(findTopLikers(userPhotoIds), limit);

		int usersToFollow = (int) (topLikers.size() * (TOP_LIKERS_PERCENTAGE_TOFOLLOW / 100.0));
		int usersToLike = topLikers.size() - usersToFollow;

		List<String> toLike = topLikers.subList(0, usersToLike);
		List<String> toFollow = topLikers.subList(usersToLike, usersToLike + usersToFollow);

		// Add toFollow users in the following queue
		dbUser.getToFollow().addAll(toFollow);

		// Add photos in the liking queue from each user in toLike users
		for (String user : toLike) {
			dbUser.getToLike().addAll(instagramPhotoService
					.getPhotos(USER, user, TOP_LIKERS_PHOTOS_TOGET, TOP_LIKERS_PHOTOS_TORETURN, true));
		}

		userRepository.saveAndFlush(dbUser);
		LOGGER.info("Added {} new users to follow from top likers for user:{}",
				toFollow.size(), username);
		LOGGER.info("Added {} new photos to to like from top likers for user:{}",
				toLike.size() * TOP_LIKERS_PHOTOS_TORETURN, username);
	}

	private Map<String, Integer> findTopLikers(List<String> userPhotoIds) {
		List<List<String>> allPhotosLikers = userPhotoIds.stream()
				.map(photo -> new IGLikesRequest(fakeIGUser)
						.getUsersWhoLiked(photo))
				.collect(Collectors.toList());

		Map<String, Integer> topLikers = new HashMap<>();

		for (List<String> photoLikers : allPhotosLikers) {
			for (String liker : photoLikers) {
				if (!topLikers.containsKey(liker))
					topLikers.put(liker, 0);

				topLikers.put(liker, topLikers.get(liker) + 1);
			}
		}

		return topLikers;
	}

	private List<String> toSortedList(Map<String, Integer> topFollowersScore, int limit) {
		LOGGER.info("Sorting top likers list..");
		return topFollowersScore.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toList()).stream()
				.map(Map.Entry::getKey)
				.limit(limit)
				.collect(Collectors.toList());
	}
}

