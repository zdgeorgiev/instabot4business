package com.instabot.api.web;

import com.instabot.api.service.InstagramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bot")
public class InstagramBotController {

	@Autowired
	private InstagramBotService instagramCommentService;


}
