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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FastExceptionsFactory implements ExceptionFactory {
    @Override
    public NotFoundException playerNotFound(long id) {
        return new NotFoundException(STR."player \{id}", false);
    }

    @Override
    public NotFoundException teamNotFound(long id) {
        return new NotFoundException(STR."team \{id}", false);
    }

    @Override
    public NotFoundException transferNotFound(long id) {
        return new NotFoundException(STR."transfer \{id}", false);
    }

    private final static PageSizeException pageSizeEx = new PageSizeException(false);

    @Override
    public PageSizeException pageSizeException() {
        return pageSizeEx;
    }

    @Override
    public PlayerTransferException orphanPlayer(FootballPlayer player) {
        return new PlayerTransferException("The player (%s %s %d) is currently not in team"
                .formatted(player.getFirstname(), player.getLastname(), player.getId()), false);
    }

    @Override
    public PlayerTransferException playerAlreadyInTeam(FootballPlayer player) {
        return new PlayerTransferException(
                "The player (%s %s %d) is already in team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), player.getCurrentTeam().getName()),
                false
        );
    }

    @Override
    public PlayerTransferException sameTeams(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Can't transfer player (%s %s %d) from team %s to team %s"
                        .formatted(player.getFirstname(), player.getLastname(), player.getId(), team.getName(), team.getName()),
                false
        );
    }

    @Override
    public PlayerTransferException teamCantAffordPlayer(FootballPlayer player, FootballTeam team) {
        return new PlayerTransferException(
                "Team %s can`t afford player (%s %s %d)"
                        .formatted(team.getName(), player.getFirstname(), player.getLastname(), player.getId()),
                false
        );
    }

    private final static ValidationException transferDateEx = new ValidationException(FootballPlayerTransferValidation.MSG_DATE_AFTER_CAREER_BEGINNING, false);

    @Override
    public ValidationException transferDate() {
        return transferDateEx;
    }

    private static final ValidationException dateOfBirthInFutureEx = new ValidationException(FootballPlayerValidation.MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST, false);
    private static final ValidationException careerBeginningBeforeBirthEx = new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE, false);
    private static final ValidationException careerBeginningInFutureEx = new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_MUST_BE_IN_PAST, false);
    private static final ValidationException playerTeamDoesNotHavePlayerEx = new ValidationException(
            FootballPlayerValidation.MSG_PLAYER_TEAM_DOES_NOT_HAVE_PLAYER, false
    );
    private static final ValidationException fewTeamsHasPlayerEx = new ValidationException(FootballPlayerValidation.MSG_FEW_TEAM_HAS_SAME_PLAYER, false);
    private static final ValidationException playerIsInTeamEx = new ValidationException(FootballPlayerValidation.MSG_PLAYER_IS_IN_TEAM, false);

    @Override
    public ValidationException dateOfBirthInFuture() {
        return dateOfBirthInFutureEx;
    }

    @Override
    public ValidationException careerBeginningBeforeBirth() {
        return careerBeginningBeforeBirthEx;
    }

    @Override
    public ValidationException careerBeginningInFuture() {
        return careerBeginningInFutureEx;
    }

    @Override
    public ValidationException playerTeamDoesNotHavePlayer() {
        return playerTeamDoesNotHavePlayerEx;
    }

    @Override
    public ValidationException fewTeamsHasPlayer() {
        return fewTeamsHasPlayerEx;
    }

    @Override
    public ValidationException playerIsInTeam() {
        return playerIsInTeamEx;
    }


    private static final ValidationException commissionBoundsEx = new ValidationException(FootballTeamValidation.MSG_COMMISSION_BOUNDS, false);
    private static final ValidationException balanceBoundsEx = new ValidationException(FootballTeamValidation.MSG_BALANCE_BOUND, false);
    private static final ValidationException teamHasForeignPlayerEx = new ValidationException(FootballTeamValidation.MSG_TEAM_HAS_FOREIGN_PLAYER, false);
    private static final ValidationException teamStillHasPlayersEx = new ValidationException(FootballTeamValidation.MSG_TEAM_STILL_HAS_PLAYERS, false);
    @Override
    public ValidationException commissionBounds() {
        return commissionBoundsEx;
    }

    @Override
    public ValidationException balanceBounds() {
        return balanceBoundsEx;
    }

    @Override
    public ValidationException teamHasForeignPlayer() {
        return teamHasForeignPlayerEx;
    }

    @Override
    public ValidationException teamStillHasPlayers() {
        return teamStillHasPlayersEx;
    }
}
