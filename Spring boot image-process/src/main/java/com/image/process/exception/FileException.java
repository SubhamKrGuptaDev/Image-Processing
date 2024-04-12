package com.image.process.exception;

public class FileException extends RuntimeException {

    public FileException() {
        super("File not Support Exception");
    }

    public FileException(String msg) {
        super(msg);
    }

}
