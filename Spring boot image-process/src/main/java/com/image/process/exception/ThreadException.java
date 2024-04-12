package com.image.process.exception;

public class ThreadException extends RuntimeException {

    public ThreadException() { super("Thread Running Exception"); }

    public ThreadException(String msg) {
        super(msg);
    }

}
