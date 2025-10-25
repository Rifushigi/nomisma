package com.rifushigi.nomisma.exception;

import java.time.Instant;

public class NotFoundException extends BaseException{

    protected NotFoundException(String message, String details) {
        super(message, details);
    }
}
