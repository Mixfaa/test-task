package com.mixfa.football_management.exception;

public class CustomizableException extends Exception {
    public CustomizableException(String message, boolean writeStacktrace) {
        super(message, null, !writeStacktrace, writeStacktrace);
    }
}
