package com.mixfa.football_management.exception;

import com.mixfa.football_management.exception.factory.ExceptionFactory;
import com.mixfa.football_management.misc.LimitedPageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

public class PageSizeException extends MyException implements HasHttpStatusCode {
    private final static String message = STR."Page size must be less than \{LimitedPageable.PAGE_SIZE_LIMIT}";

    public PageSizeException(boolean writeStacktrace) {
        super(message, writeStacktrace);
    }

    public static PageSizeException pageSizeException() {
        return exceptionFactory.pageSizeException();
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    private static volatile ExceptionFactory exceptionFactory;

    @Component
    public static class FactoryInjector {
        public FactoryInjector(ExceptionFactory factory) {
            exceptionFactory = factory;
        }
    }
}
