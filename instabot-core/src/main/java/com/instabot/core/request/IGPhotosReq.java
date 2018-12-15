package com.instabot.core.request;

import com.instabot.core.config.IGContants;
import com.instabot.core.model.IGUser;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IGPhotosReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGPhotosReq.class);

	public enum TARGET_TYPE {
		USER(IGContants.IG_PROFILE_URL),
		HASHTAG(IGContants.IG_HASHTAG_URL);

		private String typeUrl;

		TARGET_TYPE(String typeUrl) {
			this.typeUrl = typeUrl;
		}

		public String getTypeUrl() {
			return typeUrl;
		}
	}

	private IGUser user;

	public IGPhotosReq(IGUser user) {
		this.user = user;
	}

	public List<String> getPhotosFor(TARGET_TYPE targetType, String target, int limit) {

		List<String> mediaUrls = new ArrayList<>();

		user.getSeleniumIGClient().get(String.format(targetType.getTypeUrl(), target));

		while (mediaUrls.size() < limit) {

			int previousIterationPhotosCount = mediaUrls.size();

			mediaUrls.addAll(user.getSeleniumIGClient().findElements(By.className("_bz0w")).stream()
					.map(pic -> pic.findElement(By.cssSelector("a")).getAttribute("href"))
					.collect(Collectors.toList()));

			// no more photos
			if (previousIterationPhotosCount == mediaUrls.size())
				break;

			// scroll down
			user.getSeleniumIGClient().executeScript("window.scrollTo(0, document.body.scrollHeight)");

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				LOGGER.error("Cannot sleep", e);
			}
		}

		return mediaUrls.stream()
				.map(x -> x.substring(x.indexOf("p/") + 2, x.lastIndexOf("/")))
				.limit(limit)
				.collect(Collectors.toList());
	}
}
