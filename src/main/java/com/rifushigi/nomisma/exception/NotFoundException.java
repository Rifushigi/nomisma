package com.rifushigi.nomisma.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class NotFoundException extends BaseException{

    public NotFoundException(String message, String details) {
        super(message, details, HttpStatus.NOT_FOUND);
    }
}
