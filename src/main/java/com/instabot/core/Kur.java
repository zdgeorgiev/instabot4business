package com.instabot.core;

import com.instabot.core.filter.NoPhotoUserFilter;
import com.instabot.core.filter.SpamCommentFilter;
import com.instabot.core.request.IGCommentsReq;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramGetUserFollowersRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramGetUserFollowersResult;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Kur {

	private static List<String> followers = new ArrayList<>();

	public static void main(String[] args) throws IOException {

		Instagram4j user1 = login("testzdr", "1111112");

		Map<String, List<String>> userComments =
				new IGCommentsReq(user1, "Bq2b8zODozZ")
						.applyFilters(
								SpamCommentFilter.class,
								NoPhotoUserFilter.class)
						.execute();

		List<Map.Entry<String, List<String>>> list =
				new LinkedList<>(userComments.entrySet());
		list.sort(Comparator.comparingInt(o -> o.getValue().size()));
		Map<String, List<String>> sortedMap = new LinkedHashMap<>();
		for (Map.Entry<String, List<String>> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		System.out.println(userComments.size());
	}

	private static Instagram4j login(String username, String password) throws ClientProtocolException, IOException {
		Instagram4j user = Instagram4j.builder().username(username).password(password).build();
		user.setup();
		user.login();
		return user;
	}

	private static void saveFollowers(Instagram4j user, long userId)
			throws ClientProtocolException, IOException, InterruptedException {
		saveFollowers(user, userId, null);
	}

	private static void saveFollowers(Instagram4j user, long userId, String nextMaxId)
			throws ClientProtocolException, IOException, InterruptedException {
		try {
			while (true) {
				InstagramGetUserFollowersResult fr = user.sendRequest(new InstagramGetUserFollowersRequest(userId, nextMaxId));

				if (fr.getUsers().equals(null))
					break;

				followers.addAll(fr.getUsers().stream().map(x -> x.username).collect(Collectors.toList()));

				nextMaxId = fr.getNext_max_id();
				if (nextMaxId == null) {
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Is user logged? " + user.isLoggedIn());
			Thread.sleep(30000);
			saveFollowers(user, userId, nextMaxId);
		} finally {
			System.out.println(nextMaxId);
			System.out.println(followers.size());
			FileUtils.writeLines(new File("c:\\\\\\\\tmp\\\\\\\\test" + userId + ".txt"), followers);
		}
	}
}