package com.instabot.api.web;

import com.instabot.api.service.InstagramUserInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/userInfo")
public class InstagramUserInfoController {

	@Autowired
	private InstagramUserInfoService instagramUserInfoService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Return information about user queues.")
	public String getQueuesInfo() {
		return "Queue with users to follow is with size " + instagramUserInfoService.getFollowingQueueSize() +
				System.lineSeparator() +
				"Queue with photos to like is with size " + instagramUserInfoService.getLikeQueueSize();
	}
}
