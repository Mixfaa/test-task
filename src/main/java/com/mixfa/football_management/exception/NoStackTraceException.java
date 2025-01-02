package com.mixfa.football_management.exception;

public class NoStackTraceException extends Exception {
    public NoStackTraceException(String message) {
        super(message, null, false, false);
    }
}
