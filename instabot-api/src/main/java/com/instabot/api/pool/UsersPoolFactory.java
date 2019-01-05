package com.instabot.api.pool;

import com.instabot.api.model.entity.User;
import com.instabot.api.model.repository.UserRepository;
import com.instabot.core.model.FakeIGUser;
import com.instabot.core.model.IGUser;
import com.instabot.core.model.MainIGUser;
import com.instabot.core.model.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsersPoolFactory {

	@Value("${ig.main.bot.username}")
	private String MAIN_IG_USERNAME;
	@Value("${ig.main.bot.password}")
	private String MAIN_IG_PASSWORD;

	@Value("${ig.fake.bot.username}")
	private String FAKE_IG_USERNAME;
	@Value("${ig.fake.bot.password}")
	private String FAKE_IG_PASSWORD;

	@Autowired
	private UserRepository userRepository;

	private static List<IGUser> users;

	@PostConstruct
	public void init() {
		users = new ArrayList<>();
		users.add(new MainIGUser(MAIN_IG_USERNAME, MAIN_IG_PASSWORD));
		users.add(new FakeIGUser(FAKE_IG_USERNAME, FAKE_IG_PASSWORD));
		users.forEach(IGUser::login);
		createDBEntryForLoggedUser();
	}

	private void createDBEntryForLoggedUser() {
		if (userRepository.findByUsername(MAIN_IG_USERNAME) == null) {
			User mainUser = new User();
			mainUser.setUsername(MAIN_IG_USERNAME);
			userRepository.saveAndFlush(mainUser);
		}
	}

	public static IGUser getUser(UserType userType) {
		return userType == UserType.MAIN ? users.get(0) : users.get(1);
	}
}
