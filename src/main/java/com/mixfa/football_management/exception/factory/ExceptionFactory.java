package com.mixfa.football_management.exception.factory;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PageSizeException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;

/**
 * Expected to have 2 implementations -> FastExceptionsFactory with cache exceptions, create them with no stacktrace
 * And DebugExceptionsFactory -> will create new exceptions with stack trace
 */
public interface ExceptionFactory {
    NotFoundException playerNotFound(long id);

    NotFoundException teamNotFound(long id);

    NotFoundException transferNotFound(long id);

    PageSizeException pageSizeException();

    PlayerTransferException orphanPlayer(FootballPlayer player);

    PlayerTransferException playerAlreadyInTeam(FootballPlayer player);

    PlayerTransferException sameTeams(FootballPlayer player, FootballTeam team);

    PlayerTransferException teamCantAffordPlayer(FootballPlayer player, FootballTeam team);

    ValidationException transferDate();

    ValidationException dateOfBirthInFuture();

    ValidationException careerBeginningBeforeBirth();

    ValidationException careerBeginningInFuture();

    ValidationException playerTeamDoesNotHavePlayer();

    ValidationException fewTeamsHasPlayer();

    ValidationException playerIsInTeam();

    ValidationException commissionBounds();

    ValidationException balanceBounds();

    ValidationException teamHasForeignPlayer();

    ValidationException teamStillHasPlayers();

}
