package com.instabot.core.request;

import com.instabot.core.model.IGUser;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.instabot.core.config.IGContants.IG_PROFILE_URL;

public class IGPhotosReq {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGPhotosReq.class);

	private IGUser user;

	public IGPhotosReq(IGUser user) {
		this.user = user;
	}

	public Collection<String> getMediaIds(String username, int lastPhotosCount) {

		Set<String> mediaUrls = new HashSet<>();

		user.getSeleniumIGClient().get(String.format(IG_PROFILE_URL, username));

		while (mediaUrls.size() < lastPhotosCount) {

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
				.limit(lastPhotosCount)
				.collect(Collectors.toList());
	}
}
