package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.misc.ValidationErrors;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FootballTeamValidation implements ValidationErrors {
    public static final String ID_COMMISSION_BOUNDS = "commission_bounds";
    public static final String MSG_COMMISSION_BOUNDS = "Team commission must be >= 0 and <= 10";

    public static final String ID_BALANCE_BOUND = "balance_bound";
    public static final String MSG_BALANCE_BOUND = "Balance must be >= 0";

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                ValidationErrors.makeErrorId(FootballTeam.TABLE_NAME, ID_COMMISSION_BOUNDS), MSG_COMMISSION_BOUNDS,
                ValidationErrors.makeErrorId(FootballTeam.TABLE_NAME, ID_BALANCE_BOUND), MSG_BALANCE_BOUND
        );
    }
}
