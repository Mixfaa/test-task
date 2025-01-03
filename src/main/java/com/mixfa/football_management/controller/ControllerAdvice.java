package com.mixfa.football_management.controller;

import com.mixfa.football_management.exception.ErrorMessage;
import com.mixfa.football_management.exception.HasHttpStatusCode;
import com.mixfa.football_management.exception.NoStackTraceException;
import com.mixfa.football_management.misc.Utils;
import com.mixfa.football_management.service.DBLayerValidation;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerAdvice {
    private final DBLayerValidation dbLayerValidation;

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessage> onNoResourceFound(NoResourceFoundException noResourceFoundException) {
        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.NOT_FOUND.value(), "Resource not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exception) {
        log.error(exception.getLocalizedMessage());
        var errorMessage = exception instanceof NoStackTraceException noStackTraceException ?
                noStackTraceException.getLocalizedMessage() : "Internal server error";
        HttpStatusCode httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        for (var cause : Utils.iterateCauses(exception)) {
            boolean shouldBreak = false;
            switch (cause) {
                case HasHttpStatusCode withHttpCode -> {
                    httpStatusCode = withHttpCode.httpStatusCode();
                }
                case ConstraintViolationException violationException -> {
                    errorMessage = dbLayerValidation.getErrorMessage(violationException.getConstraintName());
                    httpStatusCode = HttpStatus.BAD_REQUEST;
                    shouldBreak = true;
                }
                case SQLException sqlException -> {
                    errorMessage = sqlException.getLocalizedMessage();
                    httpStatusCode = HttpStatus.BAD_REQUEST;
                    shouldBreak = true;
                }
                case jakarta.validation.ConstraintViolationException constraintViolationException -> {
                    errorMessage = constraintViolationException.getConstraintViolations()
                            .stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.joining("\n"));
                    httpStatusCode = HttpStatus.BAD_REQUEST;
                    shouldBreak = true;
                }
                default -> {
                }
            }
            if (shouldBreak)
                break;
        }

        return new ResponseEntity<>(new ErrorMessage(httpStatusCode.value(), errorMessage), httpStatusCode);
    }
}
