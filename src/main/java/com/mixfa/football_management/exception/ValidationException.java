package com.mixfa.football_management.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ValidationException extends NoStackTraceException implements HasHttpStatusCode{
    public ValidationException(String message) {
        super(message);
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
