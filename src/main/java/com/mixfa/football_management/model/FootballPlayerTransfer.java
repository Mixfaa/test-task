package com.mixfa.football_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = FootballPlayerTransfer.TABLE_NAME)
public class FootballPlayerTransfer {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private FootballPlayer transferredPlayer;

    @ManyToOne
    private FootballTeam teamFrom;
    @ManyToOne
    private FootballTeam teamTo;

    private double playerPrice;
    private double fromTeamCommission;
    private double teamFromReward;

    private LocalDateTime date;

    public record RegisterRequest(
            long playerId,
            long teamToId,
            LocalDateTime date
    ) {
    }

    public static double calculatePlayerCost(FootballPlayer player) {
        var currentDateTime = LocalDateTime.now();
        var experienceDuration = Duration.between(player.careerBeginning(), currentDateTime);
        var ageDuration = Duration.between(player.dateOfBirth(), currentDateTime);

        var expInMonth = experienceDuration.toDays() / 30.0;
        var ageInYears = ageDuration.toDays() / 365.0;

        return (expInMonth * 100000.0) / ageInYears;
    }

    public static double calculateFromTeamReward(double playerPrice, double fromTeamCommission) {
        return (fromTeamCommission * playerPrice) / 100.0;
    }

    public static final String TABLE_NAME = "football_player_transfer";
}
