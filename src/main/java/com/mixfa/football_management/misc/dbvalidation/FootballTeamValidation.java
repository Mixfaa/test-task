package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.ValidationErrors;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FootballTeamValidation implements ValidationErrors {
    public static final String ID_COMMISSION_BOUNDS = "commission_bounds";
    public static final String MSG_COMMISSION_BOUNDS = "Team commission must be >= 0 and <= 10";

    public static final String ID_BALANCE_BOUND = "balance_bound";
    public static final String MSG_BALANCE_BOUND = "Balance must be >= 0";

    public static final String MSG_TEAM_HAS_FOREIGN_PLAYER = "Team has player, that is not in team";
    public static final String MSG_TEAM_STILL_HAS_PLAYERS = "Can't delete team while it has players";

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                ValidationErrors.makeErrorId(FootballTeam.TABLE_NAME, ID_COMMISSION_BOUNDS), MSG_COMMISSION_BOUNDS,
                ValidationErrors.makeErrorId(FootballTeam.TABLE_NAME, ID_BALANCE_BOUND), MSG_BALANCE_BOUND
        );
    }

    private static final Exception commissionBoundsEx = new ValidationException(MSG_COMMISSION_BOUNDS);
    private static final Exception balanceBoundsEx = new ValidationException(MSG_BALANCE_BOUND);
    private static final Exception teamHasForeignPlayer = new ValidationException(MSG_TEAM_HAS_FOREIGN_PLAYER);
    private static final Exception teamStillHasPlayers = new ValidationException(MSG_TEAM_STILL_HAS_PLAYERS);

    private final FootballPlayerRepo footballPlayerRepo;
    private final FootballTeamRepo footballTeamRepo;

    public void preSaveValidate(FootballTeam footballTeam) throws Exception {
        if (footballTeam.getBalance() < 0.0) throw balanceBoundsEx;

        if (footballTeam.getTransferCommissionPercent() < 0.0 ||
                footballTeam.getTransferCommissionPercent() > 10.0) throw commissionBoundsEx;

        for (FootballPlayer player : footballTeam.getPlayers()) {
            if (!Objects.equals(player.getCurrentTeamId(), footballTeam.getId()))
                throw teamHasForeignPlayer;
        }
    }

    public void preDeleteValidate(long teamId) throws Exception {
        // ensure team has no players
        var team = footballTeamRepo.findById(teamId).orElseThrow(() -> NotFoundException.teamNotFound(teamId));
        if (!team.getPlayers().isEmpty())
            throw teamStillHasPlayers;
    }
}
