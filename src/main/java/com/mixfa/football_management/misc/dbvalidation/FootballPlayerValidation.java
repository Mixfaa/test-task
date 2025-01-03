package com.mixfa.football_management.misc.dbvalidation;

import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.MySQLTrigger;
import com.mixfa.football_management.misc.DbValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FootballPlayerValidation implements DbValidation {

    public static final String ID_DATE_OF_BIRTH_MUST_BE_IN_PAST = "date_of_birth_must_be_in_past";
    public static final String MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST = "Date of birth must be in the past";

    public static final String ID_CAREER_BEGINNING_MUST_BE_IN_PAST = "career_beginning_must_be_in_past";
    public static final String MSG_CAREER_BEGINNING_MUST_BE_IN_PAST = "Career beginning date must be in the past";

    public static final String ID_CAREER_BEGINNING_AFTER_BIRTH_DATE = "career_beginning_after_birth_date";
    public static final String MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE = "Career beginning must be after birth date";

    public static final String MSG_PLAYER_TEAM_DOES_NOT_HAVE_PLAYER = "Player team does not have player in composition";
    public static final String MSG_FEW_TEAM_HAS_SAME_PLAYER = "Few teams has player in composition";
    public static final String MSG_PLAYER_IS_IN_TEAM = "Can`t delete player while he is in team";

    @Override
    public Map<String, String> errorIdToMessageMap() {
        return Map.of(
                DbValidation.makeErrorId(FootballPlayer.TABLE_NAME, ID_DATE_OF_BIRTH_MUST_BE_IN_PAST), MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST,
                DbValidation.makeErrorId(FootballPlayer.TABLE_NAME, ID_CAREER_BEGINNING_MUST_BE_IN_PAST), MSG_CAREER_BEGINNING_MUST_BE_IN_PAST,
                DbValidation.makeErrorId(FootballPlayer.TABLE_NAME, ID_CAREER_BEGINNING_AFTER_BIRTH_DATE), MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE
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

    private final FootballPlayerRepo footballPlayerRepo;
    private final FootballTeamRepo footballTeamRepo;

    private static final Exception dateOfBirthInFutureEx = new ValidationException(MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST);
    private static final Exception careerBeginningBeforeBirthEx = new ValidationException(MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE);
    private static final Exception careerBeginningInFutureEx = new ValidationException(MSG_CAREER_BEGINNING_MUST_BE_IN_PAST);
    private static final Exception playerTeamDoesNotHavePlayer = new ValidationException(
            MSG_PLAYER_TEAM_DOES_NOT_HAVE_PLAYER
    );
    private static final Exception fewTeamsHasPlayer = new ValidationException(MSG_FEW_TEAM_HAS_SAME_PLAYER);
    private static final Exception playerIsInTeam = new ValidationException(MSG_PLAYER_IS_IN_TEAM);

    private static void validatePlayerParams(
            LocalDate dateOfBirth,
            LocalDate careerBeginning
    ) throws Exception {
        var currentTime = LocalDate.now();

        if (dateOfBirth.isAfter(currentTime) || dateOfBirth.isEqual(currentTime))
            throw dateOfBirthInFutureEx;

        if (careerBeginning.isBefore(dateOfBirth) ||
                careerBeginning.isEqual(dateOfBirth))
            throw careerBeginningBeforeBirthEx;

        if (careerBeginning.isAfter(currentTime) || careerBeginning.isEqual(currentTime))
            throw careerBeginningInFutureEx;
    }

    public void onSaveValidate(FootballPlayer player) throws Exception {
        validatePlayerParams(player.getDateOfBirth(), player.getCareerBeginning());
        var currentTeam = player.getCurrentTeam();
        if (currentTeam != null) {
            var teamsWithPlayer = footballTeamRepo.countAllByPlayersContains(player.getId());
            if (teamsWithPlayer == 0)
                throw playerTeamDoesNotHavePlayer;
            if (teamsWithPlayer != 1)
                throw fewTeamsHasPlayer;
        }
    }

    public void preDeleteValidate(long id) throws Exception {
        // make sure player not in any team
        var teamsWithPlayer = footballTeamRepo.countAllByPlayersContains(id);
        if (teamsWithPlayer == 0) return;
        throw playerIsInTeam;
    }
}
