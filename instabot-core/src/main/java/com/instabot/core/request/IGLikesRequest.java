package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramLikeRequest;
import org.brunocvcunha.instagram4j.util.InstagramCodeUtil;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;

public class IGLikesRequest {

	private IGUser user;

	public IGLikesRequest(IGUser user) {
		this.user = user;
	}

	public void likePhoto(String mediaId) throws IOException {
		user.getInstagram4jIGClient().sendRequest(new InstagramLikeRequest(InstagramCodeUtil.fromCode(mediaId)));
	}

	public List<String> getUsersWhoLiked(String mediaId, int maxUsersCount) {
		// TODO: implement function to return list of usernames that liked specific media id
		throw new NotImplementedException();
	}
}
