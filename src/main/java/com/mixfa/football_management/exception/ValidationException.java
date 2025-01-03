package com.mixfa.football_management.exception;


import com.mixfa.football_management.exception.factory.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

public class ValidationException extends MyException implements HasHttpStatusCode {
    public ValidationException(String message, boolean writeStacktrace) {
        super(message, writeStacktrace);
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    public static ValidationException transferDate() {
        return exceptionFactory.transferDate();
    }

    public static ValidationException dateOfBirthInFuture() {
        return exceptionFactory.dateOfBirthInFuture();
    }

    public static ValidationException careerBeginningBeforeBirth() {
        return exceptionFactory.careerBeginningBeforeBirth();
    }

    public static ValidationException careerBeginningInFuture() {
        return exceptionFactory.careerBeginningInFuture();
    }

    public static ValidationException playerTeamDoesNotHavePlayer() {
        return exceptionFactory.playerTeamDoesNotHavePlayer();
    }

    public static ValidationException fewTeamsHasPlayer() {
        return exceptionFactory.fewTeamsHasPlayer();
    }

    public static ValidationException playerIsInTeam() {
        return exceptionFactory.playerIsInTeam();
    }

    public static ValidationException commissionBounds() {
        return exceptionFactory.commissionBounds();
    }

    public static ValidationException balanceBounds() {
        return exceptionFactory.balanceBounds();
    }

    public static ValidationException teamHasForeignPlayer() {
        return exceptionFactory.teamHasForeignPlayer();
    }

    public static ValidationException teamStillHasPlayers() {
        return exceptionFactory.teamStillHasPlayers();
    }

    private static volatile ExceptionFactory exceptionFactory;

    @Component
    public static class FactoryInjector {
        public FactoryInjector(ExceptionFactory factory) {
            exceptionFactory = factory;
        }
    }
}
