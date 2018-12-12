package com.instabot.api.web;

import com.instabot.api.service.InstagramPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstagramPhotoController {

	@Autowired
	private InstagramPhotoService instagramPhotoService;

}
