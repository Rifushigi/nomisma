package com.rifushigi.nomisma.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends BaseException{

    protected ServiceUnavailableException(String message, String details) {
        super(message, details, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
