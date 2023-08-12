package com.example.springjwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SuggestionNotBelongingException extends RuntimeException {


    public SuggestionNotBelongingException(Long userId, Long suggestionId) {
        super("Suggestion with ID: " + suggestionId + " does not belong to user with ID: " + userId);
    }

}
