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
			@ApiParam(name = "mediaId", value = "MediaId id.")
			@RequestParam String mediaId) {
		instagramLikeService.likePhoto(mediaId);
	}

	@ResponseBody
	@RequestMapping(value = "/processTopLikers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Process the top likers for target based on that who likes the most "
			+ "add couple of their photos to be liked.")
	public void processTopLikers(
			@ApiParam(name = "username", value = "Target username from which we want to get its top likers.")
			@RequestParam String username,

			@ApiParam(name = "limit", value = "Top likers to return.")
			@RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit) {
		instagramLikeService.processTopLikers(username, limit);
	}
}
