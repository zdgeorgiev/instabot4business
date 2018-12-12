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
}