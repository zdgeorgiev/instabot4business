package com.instabot.api.service;

import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InstagramUserInfoService {

	@Autowired
	private UserRepository userRepository;

	private String mainIGUser = UsersPoolFactory.getUser(UserType.MAIN).getUsername();

	public int getFollowingQueueSize() {
		return userRepository.findByUsername(mainIGUser).getToFollow().size();
	}

	public int getLikeQueueSize() {
		return userRepository.findByUsername(mainIGUser).getToLike().size();
	}
}
