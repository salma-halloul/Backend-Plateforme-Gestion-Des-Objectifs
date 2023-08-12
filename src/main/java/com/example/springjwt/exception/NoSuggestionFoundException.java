package com.example.springjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuggestionFoundException extends RuntimeException {

    public NoSuggestionFoundException(Long userId) {
        super("No suggestions found for user with ID: " + userId);
    }
}

