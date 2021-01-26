package com.liuqi.exception;

public class DataParseException extends RuntimeException {
    public DataParseException() {
    }

    public DataParseException(Throwable throwable) {
        super(throwable);
    }

    public DataParseException(String s) {
        super(s);
    }

    public DataParseException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
