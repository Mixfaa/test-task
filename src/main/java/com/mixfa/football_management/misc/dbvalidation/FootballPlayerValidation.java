package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.misc.MySQLTrigger;
import com.mixfa.football_management.misc.ValidationErrors;
import com.mixfa.football_management.model.FootballPlayer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FootballPlayerValidation implements ValidationErrors {

    public static final String ID_DATE_OF_BIRTH_MUST_BE_IN_PAST = "date_of_birth_must_be_in_past";
    public static final String MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST = "Date of birth must be in the past";

    public static final String ID_CAREER_BEGINNING_MUST_BE_IN_PAST = "career_beginning_must_be_in_past";
    public static final String MSG_CAREER_BEGINNING_MUST_BE_IN_PAST = "Career beginning date must be in the past";

    public static final String ID_CAREER_BEGINNING_AFTER_BIRTH_DATE = "career_beginning_after_birth_date";
    public static final String MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE = "Career beginning must be after birth date";

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                com.mixfa.football_management.misc.ValidationErrors.makeErrorId(FootballPlayer.TABLE_NAME, ID_DATE_OF_BIRTH_MUST_BE_IN_PAST), MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST,
                com.mixfa.football_management.misc.ValidationErrors.makeErrorId(FootballPlayer.TABLE_NAME, ID_CAREER_BEGINNING_MUST_BE_IN_PAST), MSG_CAREER_BEGINNING_MUST_BE_IN_PAST,
                com.mixfa.football_management.misc.ValidationErrors.makeErrorId(FootballPlayer.TABLE_NAME, ID_CAREER_BEGINNING_AFTER_BIRTH_DATE), MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE
        );
    }


    private static String makeTriggerCode(String triggerName, boolean insertOrUpdate) {
        var method = insertOrUpdate ? "INSERT" : "UPDATE";
        return STR."""
                CREATE TRIGGER \{triggerName}
                BEFORE \{method} ON \{FootballPlayer.TABLE_NAME}
                FOR EACH ROW
                BEGIN
                    -- Check if date of birth is in the past
                    IF NEW.\{FootballPlayer.DATE_OF_BIRTH_FIELD} >= NOW() THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST}';
                    END IF;
                \s
                    -- Check if career beginning is in the past
                    IF NEW.\{FootballPlayer.CAREER_BEGINNING_FIELD} >= NOW() THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_CAREER_BEGINNING_MUST_BE_IN_PAST}';
                    END IF;
                \s
                    -- Check if career beginning is after birth date
                    IF NEW.\{FootballPlayer.CAREER_BEGINNING_FIELD} <= NEW.\{FootballPlayer.DATE_OF_BIRTH_FIELD} THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = '\{MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE}';
                    END IF;
                END;
                """;
    }

    @Override
    public List<MySQLTrigger> triggers() {
        var insertTriggerName = "footballplayer_trigger_inset";
        var updateTriggerName = "footballplayer_trigger_update";
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
}
