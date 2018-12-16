package com.instabot.api.service;

import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class InstagramHashtagService {

	@Autowired
	private UserRepository userRepository;

	private String mainUser = UsersPoolFactory.getUser(UserType.MAIN).getUsername();

	public void deleteHashtag(String hashtag) {
		User DBUser = userRepository.findByUsername(mainUser);
		DBUser.getHashtags().remove(hashtag);
		userRepository.saveAndFlush(DBUser);
	}

	public void addHashtag(String hashtag) {
		User DBUser = userRepository.findByUsername(mainUser);
		DBUser.getHashtags().add(hashtag);
		userRepository.saveAndFlush(DBUser);
	}

	public Set<String> getHashtags() {
		return userRepository.findByUsername(mainUser).getHashtags();
	}
}
