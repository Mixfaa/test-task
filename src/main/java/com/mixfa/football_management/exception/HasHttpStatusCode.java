package com.mixfa.football_management.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Interface for exception classes, defines exception HTTP status httpStatus
 */
public interface HasHttpStatusCode {
    HttpStatusCode httpStatusCode();
}
