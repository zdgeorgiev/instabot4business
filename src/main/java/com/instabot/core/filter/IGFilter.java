package com.instabot.core.filter;

public interface IGFilter<T> {

	T apply(T input);
}
