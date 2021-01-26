package com.liuqi.exception;

public class BusinessException extends RuntimeException{
    public BusinessException() {
    }

    public BusinessException(Throwable throwable) {
        super(throwable);
    }

    public BusinessException(String s) {
        super(s);
    }

    public BusinessException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
