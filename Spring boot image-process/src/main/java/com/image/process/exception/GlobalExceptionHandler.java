package com.image.process.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ColorNotSupportException.class)
    public ResponseEntity<ErrorResponse> colorNotSupportExceptionHandler(ColorNotSupportException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorResponse> fileNotFoundExceptionHandler(FileException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(PathException.class)
    public ResponseEntity<ErrorResponse> pathNotFoundExceptionHandler(PathException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(ThreadException.class)
    public ResponseEntity<ErrorResponse> threadNotFoundExceptionHandler(ThreadException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.OK);
    }

}
