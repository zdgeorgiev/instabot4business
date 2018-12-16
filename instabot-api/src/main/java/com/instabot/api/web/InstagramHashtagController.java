package com.instabot.api.web;

import com.instabot.api.service.InstagramHashtagService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = "/hashtag")
public class InstagramHashtagController {

	@Autowired
	private InstagramHashtagService instagramHashtagService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get list of hashtags for the user")
	public Set<String> getHashtags() {
		return instagramHashtagService.getHashtags();
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Add hashtag to the logged user.")
	public void addHashtag(
			@ApiParam(name = "hashtag", value = "hashtag name")
			@RequestParam String hashtag) {
		instagramHashtagService.addHashtag(hashtag);
	}

	@ResponseBody
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Delete hashtag to the logged user.")
	public void deleteHashtag(
			@ApiParam(name = "hashtag", value = "hashtag name")
			@RequestParam String hashtag) {
		instagramHashtagService.deleteHashtag(hashtag);
	}
}
