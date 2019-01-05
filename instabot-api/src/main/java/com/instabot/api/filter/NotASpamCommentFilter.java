package com.instabot.api.filter;

import com.instabot.core.filter.CommentFilter;

public class NotASpamCommentFilter implements CommentFilter {

	@Override
	public boolean apply(String comment) {
		// TODO: regex to remove the spammy comments
		return true;
	}
}
