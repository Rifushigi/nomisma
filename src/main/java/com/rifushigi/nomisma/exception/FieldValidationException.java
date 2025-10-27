package com.rifushigi.nomisma.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class FieldValidationException extends BaseException{
    public FieldValidationException(String message, Map<String, String> details) {
        super(message, details, HttpStatus.BAD_REQUEST);
    }
}
