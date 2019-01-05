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
			@ApiParam(name = "username", value = "Target username to follow.")
			@RequestParam String username) {
		instagramFollowService.follow(username);
	}

	@ResponseBody
	@RequestMapping(value = "/unfollow", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Unfollow specific user.")
	public void unfollowUser(
			@ApiParam(name = "username", value = "Target username to unfollow.")
			@RequestParam String username) {
		instagramFollowService.unfollow(username);
	}

	@ResponseBody
	@RequestMapping(value = "/processTopFollowers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Process the top followers for target based on the sorting strategy and "
			+ "add most of them to be followed and like some photos for the others.")
	public void processTopFollowers(
			@ApiParam(name = "username", value = "Target username from which we want to get its top followers.")
			@RequestParam String username,

			@ApiParam(name = "userSortingStrategy", value = "Strategy that define who is top follower.")
			@RequestParam UserSortingStrategyType userSortingStrategy,

			@ApiParam(name = "limit", value = "Top followers to return.")
			@RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit) {
		instagramFollowService.processTopFollowers(username, userSortingStrategy, limit);
	}
}
