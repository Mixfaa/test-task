package com.mixfa.football_management.exception;

import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class PlayerTransferException extends NoStackTraceException implements HasHttpStatusCode {
    public PlayerTransferException(String message) {
        super(message);
    }

    public static PlayerTransferException orphanPlayer(FootballPlayer player) {
        return new PlayerTransferException("The player (%s %s %d) is currently not in team"
                .formatted(player.getFirstname(), player.getLastname(), player.getId()));
    }

    public static PlayerTransferException playerAlreadyInTeam(FootballPlayer player) {
        return new PlayerTransferException(
                "The player (%s %s %d) is already in team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), player.getCurrentTeam().getName())
        );
    }

    public static PlayerTransferException sameTeams(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Can't transfer player (%s %s %d) from team %s to team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), team.getName(), team.getName())
        );
    }

    public static PlayerTransferException teamCantAffordPlayer(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Team %s can`t afford player (%s %s %d)"
                        .formatted(team.getName(), player.getFirstname(), player.getLastname(), player.getId())
        );
    }

    @Override
    public HttpStatusCode httpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
