package com.instabot.api.service;

import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.UserType;
import com.instabot.core.request.IGPhotosReq;
import com.instabot.core.request.IGPhotosReq.TARGET_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstagramPhotoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGPhotosReq.class);

	private IGUser fakeUser = UsersPoolFactory.getUser(UserType.FAKE);

	public List<String> getPhotos(TARGET_TYPE targetType, String target, int limit) {
		String tType = (targetType.equals(TARGET_TYPE.USER) ? "user" : "hashtag");
		LOGGER.info("Collecting {} photos for {}:{}", limit, tType, target);
		List<String> photoIds = new IGPhotosReq(fakeUser).getPhotosFor(targetType, target, limit);
		LOGGER.info("Successfully collected {} photos", photoIds.size());
		return photoIds;
	}
}
