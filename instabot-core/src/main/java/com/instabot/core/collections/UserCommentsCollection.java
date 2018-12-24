package com.instabot.core.collections;

import com.instabot.core.request.IGCommentsReq;
import com.instabot.core.strategy.UserSortingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class UserCommentsCollection {

	private static final Logger LOGGER = LoggerFactory.getLogger(IGCommentsReq.class);

	private Map<String, List<String>> userComments;
	private UserSortingStrategy strategy;

	public UserCommentsCollection() {
		userComments = new HashMap<>();
	}

	public void addComment(String username, String commentText) {
		if (!userComments.containsKey(username))
			userComments.put(username, new ArrayList<>());

		userComments.get(username).add(commentText);
	}

	public void remove(String username) {
		userComments.remove(username);
	}

	public Set<Map.Entry<String, List<String>>> entrySet() {
		return userComments.entrySet();
	}

	// Return Map<String, Integer> where String is the username
	// and the corresponding value is integer value based on the
	// value returned by the sorting strategy
	public Map<String, Integer> normalize() {
		Map<String, Integer> sortedMap = new TreeMap<>();

		userComments.forEach((user, comments) ->
				sortedMap.put(user, comments.stream().mapToInt(comment -> strategy.apply(comment)).sum()));

		return sortedMap;
	}

	public List<String> getUsers() {
		return Collections.unmodifiableList(
				userComments.entrySet().stream()
						.map(Map.Entry::getKey)
						.collect(Collectors.toList()));
	}

	public void setSortStrategy(Class<? extends UserSortingStrategy> strategy) {
		try {
			this.strategy = strategy.newInstance();
			LOGGER.debug("Using strategy {}", strategy.getName());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Cannot create strategy from " + strategy.getName());
		}
	}

	public int size() {
		return userComments.size();
	}
}
