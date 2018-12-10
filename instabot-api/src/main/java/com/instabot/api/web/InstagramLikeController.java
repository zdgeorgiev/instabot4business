package com.instabot.api.web;

import com.instabot.api.service.InstagramLikeService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/like")
public class InstagramLikeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstagramLikeController.class);

	@Autowired
	private InstagramLikeService instagramLikeService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Like a photo.")
	public void likePhoto(@RequestParam String mediaId) {
		instagramLikeService.likePhoto(mediaId);
		LOGGER.info("Liked photo with id {}", mediaId);
	}
}
