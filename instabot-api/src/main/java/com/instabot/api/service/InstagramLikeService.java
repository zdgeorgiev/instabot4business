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
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${ig.bot.api.last.user.photos.to.get:30}")
	private Integer USER_PHOTOS_TOGET;

	@Value("${ig.bot.api.top.likers.photos.to.get:3}")
	private Integer TOP_LIKERS_PHOTOS_TOGET;
	@Value("${ig.bot.api.top.likers.photos.to.return:1}")
	private Integer TOP_LIKERS_PHOTOS_TORETURN;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	private IGUser mainIGUser = UsersPoolFactory.getUser(UserType.MAIN);
	private IGUser fakeIGUser = UsersPoolFactory.getUser(UserType.FAKE);

	private String mainUsername = mainIGUser.getUsername();

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
		int photosAdded = 0;

		for (int i = 0; i < topLikers.size(); i++) {
			String user = topLikers.get(i);
			LOGGER.info("Processing user {}: ({}/{})", user, i + 1, topLikers.size());

			List<String> userPhotosToLIke = instagramPhotoService
					.getPhotos(USER, user, TOP_LIKERS_PHOTOS_TOGET, TOP_LIKERS_PHOTOS_TORETURN, true);
			dbUser.getToLike().addAll(userPhotosToLIke);
			photosAdded += userPhotosToLIke.size();
		}

		userRepository.flush();
		LOGGER.info("Added {} new photos to like from top likers for user:{}", photosAdded, username);
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

