package com.mixfa.football_management.exception.factory;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PageSizeException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerTransferValidation;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerValidation;
import com.mixfa.football_management.misc.dbvalidation.FootballTeamValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DebugExceptionsFactory implements ExceptionFactory {
    @Override
    public NotFoundException playerNotFound(long id) {
        return new NotFoundException(STR."player \{id}", true);
    }

    @Override
    public NotFoundException teamNotFound(long id) {
        return new NotFoundException(STR."team \{id}", true);
    }

    @Override
    public NotFoundException transferNotFound(long id) {
        return new NotFoundException(STR."transfer \{id}", true);
    }

    @Override
    public PageSizeException pageSizeException() {
        return new PageSizeException(true);
    }

    @Override
    public PlayerTransferException orphanPlayer(FootballPlayer player) {
        return new PlayerTransferException("The player (%s %s %d) is currently not in team"
                .formatted(player.getFirstname(), player.getLastname(), player.getId()), true);
    }

    @Override
    public PlayerTransferException playerAlreadyInTeam(FootballPlayer player) {
        return new PlayerTransferException(
                "The player (%s %s %d) is already in team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), player.getCurrentTeam().getName()),
                true
        );
    }

    @Override
    public PlayerTransferException sameTeams(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Can't transfer player (%s %s %d) from team %s to team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), team.getName(), team.getName()),
                true
        );
    }

    @Override
    public PlayerTransferException teamCantAffordPlayer(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Team %s can`t afford player (%s %s %d)"
                        .formatted(team.getName(), player.getFirstname(), player.getLastname(), player.getId()),
                true
        );
    }

    @Override
    public ValidationException transferDate() {
        return new ValidationException(FootballPlayerTransferValidation.MSG_DATE_AFTER_CAREER_BEGINNING, true);
    }


    @Override
    public ValidationException dateOfBirthInFuture() {
        return new ValidationException(FootballPlayerValidation.MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST, true);
    }

    @Override
    public ValidationException careerBeginningBeforeBirth() {
        return new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE, true);
    }

    @Override
    public ValidationException careerBeginningInFuture() {
        return new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_MUST_BE_IN_PAST, true);
    }

    @Override
    public ValidationException playerTeamDoesNotHavePlayer() {
        return new ValidationException(
                FootballPlayerValidation.MSG_PLAYER_TEAM_DOES_NOT_HAVE_PLAYER, true
        );
    }

    @Override
    public ValidationException fewTeamsHasPlayer() {
        return new ValidationException(FootballPlayerValidation.MSG_FEW_TEAM_HAS_SAME_PLAYER, true);
    }

    @Override
    public ValidationException playerIsInTeam() {
        return new ValidationException(FootballPlayerValidation.MSG_PLAYER_IS_IN_TEAM, true);
    }

    @Override
    public ValidationException commissionBounds() {
        return new ValidationException(FootballTeamValidation.MSG_COMMISSION_BOUNDS, true);
    }

    @Override
    public ValidationException balanceBounds() {
        return new ValidationException(FootballTeamValidation.MSG_BALANCE_BOUND, true);
    }

    @Override
    public ValidationException teamHasForeignPlayer() {
        return new ValidationException(FootballTeamValidation.MSG_TEAM_HAS_FOREIGN_PLAYER, true);
    }

    @Override
    public ValidationException teamStillHasPlayers() {
        return new ValidationException(FootballTeamValidation.MSG_TEAM_STILL_HAS_PLAYERS, true);
    }
}
