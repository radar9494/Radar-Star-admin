package com.liuqi.exception;

public class NotGetLockException extends RuntimeException {

	public NotGetLockException() {
		super();
	}

	public NotGetLockException(String message) {
		super(message);
	}

	public NotGetLockException(String message, Throwable cause) {
		super(message, cause);
	}
}
