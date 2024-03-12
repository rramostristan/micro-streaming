package com.test.microstreaming.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String mesagge) {
        super(mesagge, HttpStatus.NOT_FOUND);
    }
}
