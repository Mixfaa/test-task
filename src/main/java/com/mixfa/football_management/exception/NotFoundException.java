package com.mixfa.football_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotFoundException extends NoStackTraceException implements HasHttpStatusCode {
    public NotFoundException(String entity) {
        super("Entity (%s) not found".formatted(entity));
    }

    public static NotFoundException playerNotFound(long id) {
        return new NotFoundException("player " + id);
    }

    public static NotFoundException teamNotFound(long id) {
        return new NotFoundException("Team " + id);
    }

    public static NotFoundException transferNotFound(long id){
        return new NotFoundException("Transfer " + id);
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.NOT_FOUND;
    }
}
