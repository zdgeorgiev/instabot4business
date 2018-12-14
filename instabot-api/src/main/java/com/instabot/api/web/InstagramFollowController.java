package com.instabot.api.web;

import com.instabot.api.model.UserSortingStrategyType;
import com.instabot.api.service.InstagramFollowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/follow")
public class InstagramFollowController {

	@Autowired
	private InstagramFollowService instagramFollowService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Follow user by its username.")
	public void followUser(
			@ApiParam(name = "username", value = "Username")
			@RequestParam String username) {
		instagramFollowService.follow(username);
	}

	@ResponseBody
	@RequestMapping(value = "/top", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get a list of top followers for a specific user that never have been followed.")
	public List<String> getTopNotEverFollowedFollowers(
			@ApiParam(name = "targetUsername", value = "Target is the user from who we want to get its top followers")
			@RequestParam String targetUsername,

			@ApiParam(name = "userSortingStrategy", value = "Strategy that define who is top follower")
			@RequestParam UserSortingStrategyType userSortingStrategy,

			@ApiParam(name = "topFollowersCount", value = "Top followers count")
			@RequestParam(value = "topFollowersCount", required = false, defaultValue = "100") Integer bestUsersCount) {

		return instagramFollowService
				.getTopNotEverFollowedFollowers(targetUsername, userSortingStrategy.getStrategyClass(), bestUsersCount);
	}
}
