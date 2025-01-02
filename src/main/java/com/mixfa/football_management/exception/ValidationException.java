package com.mixfa.football_management.exception;

public class ValidationException extends NoStackTraceException{
    public ValidationException(String message) {
        super(message);
    }
}
