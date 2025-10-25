package com.rifushigi.nomisma.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class BaseException extends RuntimeException{

    private final String details;
    private final Instant timestamp;
    private final HttpStatus status;

    protected BaseException(String message, String details, HttpStatus status){
        super(message);
        this.details = details;
        this.status = status;
        this.timestamp = Instant.now();
    }
}
