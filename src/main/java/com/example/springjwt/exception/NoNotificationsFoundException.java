package com.example.springjwt.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoNotificationsFoundException extends RuntimeException {
    public NoNotificationsFoundException(String message) {
        super(message);
    }
}
