package com.instabot.api.service;

import com.instabot.core.IGClient;
import org.springframework.stereotype.Service;

@Service
public class InstagramLikeService {

	public void likePhoto(String mediaId) {
		IGClient.likePhoto(mediaId);
	}
}
