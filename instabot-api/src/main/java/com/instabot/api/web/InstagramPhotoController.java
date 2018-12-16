package com.instabot.api.web;

import com.instabot.api.service.InstagramPhotoService;
import com.instabot.core.request.IGPhotosReq.TARGET_TYPE;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/photo")
public class InstagramPhotoController {

	@Autowired
	private InstagramPhotoService instagramPhotoService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Get photos for specific type.")
	public List<String> getPhotosForTargetType(
			@ApiParam(name = "target", value = "Target name")
			@RequestParam String target,

			@ApiParam(name = "targetType", value = "Photo for user or hashtag")
			@RequestParam(name = "targetType", defaultValue = "USER") TARGET_TYPE targetType,

			@ApiParam(name = "limit", value = "max photos to return")
			@RequestParam(name = "limit", required = false, defaultValue = "100") Integer limit) {
		return instagramPhotoService.getPhotos(targetType, target, limit);
	}
}
