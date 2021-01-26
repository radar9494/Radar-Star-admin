package com.liuqi.exception;

public class BaseException extends RuntimeException{
    public BaseException() {
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }

    public BaseException(String s) {
        super(s);
    }

    public BaseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
