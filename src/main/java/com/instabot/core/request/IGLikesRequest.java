package com.instabot.core.request;

import com.instabot.core.client.instagram4j.Instagram4jIG;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramLikeRequest;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class IGLikesRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGLikesRequest.class);

	private Instagram4j instagram4jClient;

	public IGLikesRequest() {
		this.instagram4jClient = Instagram4jIG.getClient();
	}

	public void likePhoto(String mediaId) {
		try {
			instagram4jClient.sendRequest(new InstagramLikeRequest(InstagramCodeUtil.fromCode(mediaId)));
		} catch (IOException e) {
			LOGGER.error("Cannot like photo: {}", mediaId, e);
		}
	}

	public List<String> getUsersWhoLiked(String mediaId, int count) {
		return null;
	}
}
