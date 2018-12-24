package com.instabot.core.filter;

public class NotASpamCommentFilter implements CommentFilter {

	@Override
	public boolean apply(String comment) {
		// TODO: regex to remove the spammy comments
		return true;
	}
}
