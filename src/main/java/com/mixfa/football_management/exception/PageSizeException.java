package com.mixfa.football_management.exception;

import com.mixfa.football_management.misc.LimitedPageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PageSizeException extends NoStackTraceException implements HasHttpStatusCode {
    private PageSizeException(String message) {
        super(message);
    }
 
    private static final PageSizeException PAGE_SIZE_EXCEPTION = new PageSizeException("Page size must be less than " + LimitedPageable.PAGE_SIZE_LIMIT);

    public static PageSizeException pageSizeException() {
        return PAGE_SIZE_EXCEPTION;
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
