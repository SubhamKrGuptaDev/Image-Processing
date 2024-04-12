package com.image.process.exception;

public class ColorNotSupportException extends RuntimeException {

    public ColorNotSupportException() {
        super("Color Not Support Exception");
    }

    public ColorNotSupportException(String msg) {
        super(msg);
    }

}
