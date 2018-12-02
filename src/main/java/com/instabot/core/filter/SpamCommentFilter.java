package com.instabot.core.filter;

public class SpamCommentFilter implements CommentFilter {

	@Override
	public String apply(String comment) {

		return comment;
	}
}
