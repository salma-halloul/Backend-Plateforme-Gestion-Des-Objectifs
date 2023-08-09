package com.example.springjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectiveNotFoundException extends RuntimeException {

    public ObjectiveNotFoundException(String message) {
        super(message);
    }
}
