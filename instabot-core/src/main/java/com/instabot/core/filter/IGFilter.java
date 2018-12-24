package com.instabot.core.filter;

public interface IGFilter<T> {

	boolean apply(T input);
}
