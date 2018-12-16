package com.instabot.api.config;

import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.api.pool.UsersPoolFactory;
import com.instabot.core.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class InitialUserRegistrationBean {

	@Autowired
	private UserRepository userRepository;

	@PostConstruct
	public void registerLoggedUser() {
		String mainUsername = UsersPoolFactory.getUser(UserType.MAIN).getUsername();

		if (userRepository.findByUsername(mainUsername) == null) {
			User mainUser = new User();
			mainUser.setUsername(mainUsername);
			userRepository.saveAndFlush(mainUser);
		}
	}
}
