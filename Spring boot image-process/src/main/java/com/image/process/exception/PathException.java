package com.image.process.exception;

public class PathException extends RuntimeException {

    public PathException() {
        super("Path Exception");
    }

    public PathException(String msg) {
        super(msg);
    }

}
