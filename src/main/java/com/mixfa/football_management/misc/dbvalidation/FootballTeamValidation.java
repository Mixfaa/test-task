package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.DbValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FootballTeamValidation implements DbValidation {
    public static final String ID_COMMISSION_BOUNDS = "commission_bounds";
    public static final String MSG_COMMISSION_BOUNDS = "Team commission must be >= 0 and <= 10";

    public static final String ID_BALANCE_BOUND = "balance_bound";
    public static final String MSG_BALANCE_BOUND = "Balance must be >= 0";

    public static final String MSG_TEAM_HAS_FOREIGN_PLAYER = "Team has player, that is not in team";
    public static final String MSG_TEAM_STILL_HAS_PLAYERS = "Can't delete team while it has players";

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                DbValidation.makeErrorId(FootballTeam.TABLE_NAME, ID_COMMISSION_BOUNDS), MSG_COMMISSION_BOUNDS,
                DbValidation.makeErrorId(FootballTeam.TABLE_NAME, ID_BALANCE_BOUND), MSG_BALANCE_BOUND
        );
    }

    private final FootballTeamRepo footballTeamRepo;

    public void onSaveValidate(FootballTeam footballTeam) throws Exception {
        if (footballTeam.getBalance() < 0.0) throw ValidationException.balanceBounds();

        if (footballTeam.getTransferCommissionPercent() < 0.0 ||
                footballTeam.getTransferCommissionPercent() > 10.0) throw ValidationException.commissionBounds();

        for (FootballPlayer player : footballTeam.getPlayers()) {
            if (!Objects.equals(player.getCurrentTeamId(), footballTeam.getId()))
                throw ValidationException.teamHasForeignPlayer();
        }
    }

    public void preDeleteValidate(long teamId) throws Exception {
        // ensure team has no players
        var team = footballTeamRepo.findById(teamId).orElseThrow(() -> NotFoundException.teamNotFound(teamId));
        if (!team.getPlayers().isEmpty())
            throw ValidationException.teamStillHasPlayers();
    }
}
