package com.mixfa.football_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
@Table(name = FootballPlayerTransfer.TABLE_NAME)
public class FootballPlayerTransfer {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "player_record_id", nullable = false)
    private FootballPlayerRecord playerRecord;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "team_from_record_id", nullable = false)
    private FootballTeamRecord teamFromRecord;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "team_to_record_id", nullable = false)
    private FootballTeamRecord teamToRecord;

    private double playerPrice;
    private double teamFromCommission;
    private double teamFromReward;

    private LocalDate date;

    public record RegisterRequest(
            long playerId,
            long teamToId,
            LocalDate date
    ) {
    }

    public static double calculatePlayerCost(FootballPlayer player) {
        var currentDate = LocalDate.now();

        var expInMonth = ChronoUnit.MONTHS.between(player.getCareerBeginning(), currentDate);
        var ageInYears = ChronoUnit.DAYS.between(player.getDateOfBirth(), currentDate);

        return (expInMonth * 100000.0) / ageInYears;
    }

    public static double calculateFromTeamReward(double playerPrice, double fromTeamCommission) {
        return (fromTeamCommission * playerPrice) / 100.0;
    }

    public static final String PLAYER_RECORD_ID_FIELD = "player_record_id";
    public static final String TEAM_FROM_RECORD_ID_FIELD = "team_from_record_id";
    public static final String TEAM_TO_RECORD_ID_FIELD = "team_to_record_id";
    public static final String PLAYER_PRICE_FIELD = "player_price";
    public static final String TEAM_FROM_COMMISSION_FIELD = "team_from_commission";
    public static final String TEAM_FROM_REWARD_FIELD = "team_from_reward";
    public static final String DATE_FIELD = "date";
    public static final String TABLE_NAME = "football_player_transfer";
}
