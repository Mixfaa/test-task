package com.mixfa.football_management.exception;

public record ErrorMessage(
        int httpStatus,
        String message
) {
}
