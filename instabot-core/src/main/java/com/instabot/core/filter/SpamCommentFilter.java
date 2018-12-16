package com.instabot.core.filter;

public class SpamCommentFilter implements CommentFilter {

	@Override
	public String apply(String comment) {
		// TODO: regex to remove the spammy comments
		return comment;
	}
}
