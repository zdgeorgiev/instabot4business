package com.instabot.api.filter;

import com.instabot.core.filter.CommentFilter;

public class NotASpamCommentFilter implements CommentFilter {

	@Override
	public boolean apply(String comment) {
		// Remove 'like back' (lb) comments.
		// TODO: find a better way to remove the because we can remove comment
		// TODO: with comment like @lb... which will be a mention.. but what ever..
		return !comment.toLowerCase().contains("lb");
	}
}
