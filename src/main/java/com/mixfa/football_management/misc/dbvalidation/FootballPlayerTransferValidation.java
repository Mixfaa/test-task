package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.DbValidation;
import com.mixfa.football_management.misc.MySQLTrigger;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class FootballPlayerTransferValidation implements DbValidation {
    public static final String ID_SAME_TEAMS = "teams_must_be_different";
    public static final String MSG_SAME_TEAMS = "Teams must be different";

    public static final String ID_FROM_TEAM_COMMISSION = "from_team_commission";
    public static final String MSG_FROM_TEAM_COMMISSION = "Team commission must be >= 0 and <= 10";

    public static final String ID_FROM_TEAM_REWARD = "from_team_reward";
    public static final String MSG_FROM_TEAM_REWARD = "Team reward must be >= 0";

    public static final String ID_DATE_AFTER_CAREER_BEGINNING = "date_after_career";
    public static final String MSG_DATE_AFTER_CAREER_BEGINNING = "Transfer date must be after player career beginning and in the past";

    private static String makeTriggerCode(String triggerName, boolean insertOrUpdate) {
        var method = insertOrUpdate ? "INSERT" : "UPDATE";

        return STR."""
                CREATE TRIGGER \{triggerName}
                BEFORE \{method} ON \{FootballPlayerTransfer.TABLE_NAME}
                FOR EACH ROW
                BEGIN
                    DECLARE career_start DATETIME;
                   \s
                    -- Get player's career beginning date
                    SELECT \{FootballPlayer.CAREER_BEGINNING_FIELD} INTO career_start
                    FROM \{FootballPlayer.TABLE_NAME}
                    WHERE id = NEW.id;
                   \s
                    -- Check if teams are different
                    IF NEW.\{FootballPlayerTransfer.TEAM_FROM_ID_FIELD} = NEW.\{FootballPlayerTransfer.TEAM_TO_ID_FIELD} THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_SAME_TEAMS}';
                    END IF;
                   \s
                    -- Check from_team commission (between 0 and 10)
                    IF NEW.\{FootballPlayerTransfer.TEAM_FROM_COMMISSION_FIELD} < 0 OR NEW.\{FootballPlayerTransfer.TEAM_FROM_COMMISSION_FIELD} > 10 THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_FROM_TEAM_COMMISSION}';
                    END IF;
                   \s
                    -- Check from_team reward (>= 0)
                    IF NEW.\{FootballPlayerTransfer.TEAM_FROM_REWARD_FIELD} < 0 THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_FROM_TEAM_REWARD}';
                    END IF;
                   \s
                    -- Check if transfer date is after career beginning
                    IF NEW.\{FootballPlayerTransfer.DATE_FIELD} <= career_start OR NEW.\{FootballPlayerTransfer.DATE_FIELD} >= NOW() THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_DATE_AFTER_CAREER_BEGINNING}';
                    END IF;
                END;
                """;
    }

    @Override
    public List<MySQLTrigger> triggers() {
        var insertTriggerName = "before_transfer_insert";
        var updateTriggerName = "before_transfer_update";

        return List.of(
                new MySQLTrigger(
                        insertTriggerName,
                        makeTriggerCode(insertTriggerName, true)
                ),
                new MySQLTrigger(
                        updateTriggerName,
                        makeTriggerCode(updateTriggerName, false)
                )
        );
    }

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                DbValidation.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_SAME_TEAMS), MSG_SAME_TEAMS,
                DbValidation.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_FROM_TEAM_COMMISSION), MSG_FROM_TEAM_COMMISSION,
                DbValidation.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_FROM_TEAM_REWARD), MSG_FROM_TEAM_REWARD,
                DbValidation.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_DATE_AFTER_CAREER_BEGINNING), MSG_DATE_AFTER_CAREER_BEGINNING
        );
    }

    private static final Exception transferDateEx = new ValidationException(MSG_DATE_AFTER_CAREER_BEGINNING);

    public void onSaveValidate(FootballPlayerTransfer transfer) throws Exception {
        var currentDate = LocalDate.now();
        var player = transfer.getTransferredPlayer();

        if (transfer.getDate().isBefore(player.getCareerBeginning()) || transfer.getDate().isAfter(currentDate))
            throw transferDateEx;

        var playerTeamId = transfer.getTransferredPlayer().getCurrentTeamId();
        var teamTo = transfer.getTeamTo();
        var teamFrom = transfer.getTeamFrom();
        var playerPrice = transfer.getPlayerPrice();
        var teamFromReward = transfer.getTeamFromReward();

        if (playerTeamId.equals(teamTo.getId()))
            throw PlayerTransferException.playerAlreadyInTeam(player);

        if (teamFrom.getId().equals(teamTo.getId()))
            throw PlayerTransferException.sameTeams(player, teamFrom);
        var totalTransferPrice = playerPrice + teamFromReward;

        var buyerTeamBalance = teamTo.getBalance();
        if (buyerTeamBalance < totalTransferPrice)
            throw PlayerTransferException.teamCantAffordPlayer(player, teamTo);
    }

    public void preDeleteValidate(long teamId) throws Exception {

    }
}
