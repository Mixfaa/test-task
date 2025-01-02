package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.misc.MySQLTrigger;
import com.mixfa.football_management.misc.ValidationErrors;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FootballPlayerTransferValidation implements ValidationErrors {
    public static final String ID_SAME_TEAMS = "teams_must_be_different";
    public static final String MSG_SAME_TEAMS = "Teams must be different";

    public static final String ID_FROM_TEAM_COMMISSION = "from_team_commission";
    public static final String MSG_FROM_TEAM_COMMISSION = "Team commission must be >= 0 and <= 10";

    public static final String ID_FROM_TEAM_REWARD = "from_team_reward";
    public static final String MSG_FROM_TEAM_REWARD = "Team reward must be >= 0";

    public static final String ID_DATE_AFTER_CAREER_BEGINNING = "date_after_career";
    public static final String MSG_DATE_AFTER_CAREER_BEGINNING = "Transfer date must be after player career beginning";

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
                    SELECT career_beginning INTO career_start
                    FROM football_player
                    WHERE id = NEW.id;
                   \s
                    -- Check if teams are different
                    IF NEW.team_from_id = NEW.team_to_id THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_SAME_TEAMS}';
                    END IF;
                   \s
                    -- Check from_team commission (between 0 and 10)
                    IF NEW.from_team_commission < 0 OR NEW.from_team_commission > 10 THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_FROM_TEAM_COMMISSION}';
                    END IF;
                   \s
                    -- Check from_team reward (>= 0)
                    IF NEW.team_from_reward < 0 THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_FROM_TEAM_REWARD}';
                    END IF;
                   \s
                    -- Check if transfer date is after career beginning
                    IF NEW.date <= career_start THEN
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
                ValidationErrors.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_SAME_TEAMS), MSG_SAME_TEAMS,
                ValidationErrors.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_FROM_TEAM_COMMISSION), MSG_FROM_TEAM_COMMISSION,
                ValidationErrors.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_FROM_TEAM_REWARD), MSG_FROM_TEAM_REWARD,
                ValidationErrors.makeErrorId(FootballPlayerTransfer.TABLE_NAME, ID_DATE_AFTER_CAREER_BEGINNING), MSG_DATE_AFTER_CAREER_BEGINNING
        );
    }
}
