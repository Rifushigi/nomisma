package com.rifushigi.nomisma.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@Getter
public class BaseException extends RuntimeException{

    private final Object details;
    private final Instant timestamp;
    private final HttpStatus status;

    protected BaseException(String message, String details, HttpStatus status){
        super(message);
        this.details = details;
        this.status = status;
        this.timestamp = Instant.now();
    }

    protected BaseException(String message, Map<String, String> details, HttpStatus status){
        super(message);
        this.details = details;
        this.status = status;
        this.timestamp = Instant.now();
    }
}
