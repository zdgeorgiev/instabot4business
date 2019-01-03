package com.instabot.api.web;

import com.instabot.api.service.InstagramBotService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bot")
public class InstagramBotController {

	@Autowired
	private InstagramBotService instagramBotService;

	// Every day at 5PM
	@Scheduled(cron = "0 0 17 * * *")
	@RequestMapping(value = "/cleanFollowings", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Clean following users")
	public void cleanFollowingUsers() {
		instagramBotService.cleanFollowingUsers();
	}

	// Every day at 5PM
	@Scheduled(cron = "0 0 17 * * *")
	@RequestMapping(value = "/followUsers", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Start following users from the following queue")
	public void followUsers() {
		instagramBotService.followUsers();
	}

	// TODO:MAYBE TWO SCHEDULED CANNOT BE EXECUTED AT ONCE AND SECOND IS WAITING THE FIRST TO FINISH ?

	// Every day at 5PM
	@Scheduled(cron = "0 0 17 * * *")
	@RequestMapping(value = "/likePhotos", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Start liking photos from the liking queue")
	public void likePhotos() {
		instagramBotService.likePhotos();
	}

	// Every day every hour
	@Scheduled(cron = "0 0 * * * *")
	@RequestMapping(value = "/addPhotosFromHashtag", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Scan hashtags for the user and add photos for liking in the liking queue")
	public void addPhotosFromHashtag() {
		instagramBotService.addPhotosFromHashtag();
	}
}
