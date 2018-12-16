package com.instabot.api.web;

import com.instabot.api.model.UserSortingStrategyType;
import com.instabot.api.service.InstagramFollowService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
	@RequestMapping(value = "/addFollowers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Add top followers for a user in the following queue for the current user")
	public void addTopTargetFollowers(
			@ApiParam(name = "username", value = "Target username from which we want to get its top followers")
			@RequestParam String username,

			@ApiParam(name = "userSortingStrategy", value = "Strategy that define who is top follower")
			@RequestParam UserSortingStrategyType userSortingStrategy) {
		instagramFollowService.addTopTargetFollowers(username, userSortingStrategy);
	}
}
