package com.instabot.core.model.exception;

public class CannotInitializeClientException extends RuntimeException {

	public CannotInitializeClientException() {
	}

	public CannotInitializeClientException(String message) {
		super(message);
	}

	public CannotInitializeClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
