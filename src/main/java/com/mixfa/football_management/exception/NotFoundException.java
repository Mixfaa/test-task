package com.mixfa.football_management.exception;

import com.mixfa.football_management.exception.factory.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

public class NotFoundException extends CustomizableException implements HasHttpStatusCode {
    public NotFoundException(String entity, boolean writeStackTrace) {
        super("Entity (%s) not found".formatted(entity), writeStackTrace);
    }

    public static NotFoundException playerNotFound(long id) {
        return exceptionFactory.playerNotFound(id);
    }

    public static NotFoundException teamNotFound(long id) {
        return exceptionFactory.teamNotFound(id);
    }

    public static NotFoundException transferNotFound(long id) {
        return exceptionFactory.transferNotFound(id);
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.NOT_FOUND;
    }

    private static volatile ExceptionFactory exceptionFactory;

    @Component
    public static class FactoryInjector {
        public FactoryInjector(ExceptionFactory factory) {
            exceptionFactory = factory;
        }
    }
}
