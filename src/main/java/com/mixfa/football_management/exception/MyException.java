package com.mixfa.football_management.exception;

public class MyException extends Exception {
    public MyException(String message, boolean writeStacktrace) {
        super(message, null, !writeStacktrace, writeStacktrace);
    }
}
