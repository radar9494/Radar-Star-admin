package com.liuqi.exception;

public class InstanceException extends RuntimeException {
    public InstanceException() {
    }

    public InstanceException(String s) {
        super(s);
    }

    public InstanceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InstanceException(Throwable throwable) {
        super(throwable);
    }
}
