package com.instabot.core.model.exception;

public class CannotInitializeDriverException extends RuntimeException {

	public CannotInitializeDriverException() {
	}

	public CannotInitializeDriverException(String message) {
		super(message);
	}

	public CannotInitializeDriverException(String message, Throwable cause) {
		super(message, cause);
	}
}
