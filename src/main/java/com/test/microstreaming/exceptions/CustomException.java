package com.test.microstreaming.exceptions;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    protected HttpStatus status;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}