package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramGetMediaLikersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramLikeRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetMediaLikersResult;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class IGLikesRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGLikesRequest.class);

	private IGUser user;

	public IGLikesRequest(IGUser user) {
		this.user = user;
	}

	public void likePhoto(String mediaId) throws IOException {
		user.getInstagram4jIGClient().sendRequest(new InstagramLikeRequest(InstagramCodeUtil.fromCode(mediaId)));
	}

	public List<String> getUsersWhoLiked(String mediaId) {

		long mediaCode = InstagramCodeUtil.fromCode(mediaId);
		LOGGER.info("Starting to collect likes for media: {}", mediaCode);

		try {
			// Note that this will return maximum ~1000 users only
			// but i thing its ok to use this rather than the selenium way
			InstagramGetMediaLikersResult likersResult = user.getInstagram4jIGClient()
					.sendRequest(new InstagramGetMediaLikersRequest(mediaCode));

			return likersResult.getUsers().stream().map(user -> user.username).collect(Collectors.toList());
		} catch (IOException e) {
			LOGGER.error("Cannot get users who liked {}", mediaId, e);
			throw new RuntimeException(e);
		}
	}
}
