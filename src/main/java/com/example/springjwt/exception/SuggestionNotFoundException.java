package com.example.springjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SuggestionNotFoundException extends RuntimeException {

    public SuggestionNotFoundException(Long suggestionId) {
        super("Suggestion not found with ID: " + suggestionId);
    }

}
