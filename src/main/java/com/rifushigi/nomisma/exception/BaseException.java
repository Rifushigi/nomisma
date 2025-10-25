package com.rifushigi.nomisma.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class BaseException extends RuntimeException{

    private final String details;
    private final Instant timestamp;

    protected BaseException(String message, String details){
        super(message);
        this.details = details;
        this.timestamp = Instant.now();
    }
}
