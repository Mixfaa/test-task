package com.mixfa.football_management.exception;

import com.mixfa.football_management.exception.factory.ExceptionFactory;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

public class PlayerTransferException extends MyException implements HasHttpStatusCode {
    public PlayerTransferException(String message, boolean writeStacktrace) {
        super(message, writeStacktrace);
    }

    public static PlayerTransferException orphanPlayer(FootballPlayer player) {
        return exceptionFactory.orphanPlayer(player);
    }

    public static PlayerTransferException playerAlreadyInTeam(FootballPlayer player) {
        return exceptionFactory.playerAlreadyInTeam(player);
    }

    public static PlayerTransferException sameTeams(FootballPlayer player, FootballTeam team) {
        return exceptionFactory.sameTeams(player,team);
    }

    public static PlayerTransferException teamCantAffordPlayer(FootballPlayer player, FootballTeam team) {
        return exceptionFactory.teamCantAffordPlayer(player, team);
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
