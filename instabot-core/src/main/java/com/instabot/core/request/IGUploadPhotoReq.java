package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.brunocvcunha.instagram4j.requests.InstagramUploadPhotoRequest;

import java.io.File;
import java.io.IOException;

public class IGUploadPhotoReq {

	private IGUser user;

	public IGUploadPhotoReq(IGUser user) {
		this.user = user;
	}

	public void uploadPhoto(File photo, String description) throws IOException {

		if(photo == null) {
			throw new IllegalArgumentException("Photo to upload cannot be null");
		}

		this.user.getInstagram4jIGClient().sendRequest(new InstagramUploadPhotoRequest(photo, description));
	}
}
