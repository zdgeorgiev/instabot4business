package com.instabot.api.web;

import com.instabot.api.service.InstagramLikeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/like")
public class InstagramLikeController {

	@Autowired
	private InstagramLikeService instagramLikeService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Like a photo.")
	public void likePhoto(
			@ApiParam(name = "photo", value = "Photo id")
			@RequestParam String mediaId) {
		instagramLikeService.likePhoto(mediaId);
	}

	@ResponseBody
	@RequestMapping(value = "/addLikers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Add top likers for a user in the liking queue for the current user")
	public void addTopTargetLikers(
			@ApiParam(name = "username", value = "Target username from which we want to get its top likers")
			@RequestParam String username) {
		instagramLikeService.addTopTargetLikers(username);
	}
}
