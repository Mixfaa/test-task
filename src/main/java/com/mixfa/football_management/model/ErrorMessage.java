package com.mixfa.football_management.model;

public record ErrorMessage(
        int httpStatus,
        String message
) {
}
