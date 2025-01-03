package com.mixfa.football_management.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mixfa.football_management.misc.dbvalidation.FootballTeamValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = FootballTeam.TABLE_NAME)
public class FootballTeam {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 128)
    private String name;

    @Check(name = FootballTeamValidation.ID_COMMISSION_BOUNDS, constraints = TRANSFER_COMMISSION_FIELD + " >= 0 AND " + TRANSFER_COMMISSION_FIELD + " <= 10")
    private double transferCommissionPercent; // 0 - 10%
    @Check(name = FootballTeamValidation.ID_BALANCE_BOUND, constraints = BALANCE_FIELD + " >= 0")
    private double balance;

    @OneToMany(mappedBy = "currentTeam")
    @JsonManagedReference
    private Set<FootballPlayer> players;

    public FootballTeam(String name, double commission, double balance) {
        this.id = null;
        this.name = name;
        this.transferCommissionPercent = commission;
        this.balance = balance;
        this.players = new HashSet<>();
    }

    public FootballTeam(long id, String name, double commission, double balance) {
        this.id = id;
        this.name = name;
        this.transferCommissionPercent = commission;
        this.balance = balance;
        this.players = new HashSet<>();
    }

    public FootballTeam(RegisterRequest registerRequest) {
        this(
                null,
                registerRequest.name,
                registerRequest.transferCommission,
                registerRequest.balance,
                new HashSet<>()
        );
    }

    public void addPlayer(FootballPlayer player) {
        players.add(player);
        player.setCurrentTeam(this);
    }

    public void addPlayers(Collection<FootballPlayer> players) {
        players.forEach(this::addPlayer);
    }

    public void removePlayer(FootballPlayer player) {
        players.remove(player);
        player.setCurrentTeam(null);
    }

    public record RegisterRequest(
            @NotBlank(message = "Name must not be empty")
            String name,
            @Min(value = 0, message = "Transfer commission must be >= 0")
            @Max(value = 10, message = "Transfer commission must be <= 10")
            double transferCommission,
            @Min(value = 0, message = "Team balance must be >= 0")
            double balance,
            Set<Long> playerIds
    ) {
    }

    public record UpdateRequest(
            @NotBlank(message = "Name must not be empty")
            String name,
            @Min(value = 0, message = "Transfer commission must be >= 0")
            @Max(value = 10, message = "Transfer commission must be <= 10")
            double transferCommission,
            @Min(value = 0, message = "Team balance must be >= 0")
            double balance,
            Set<Long> playerIds
    ) {
        public UpdateRequest(FootballTeam team) {
            this(team.name, team.transferCommissionPercent, team.balance, team.players.stream().map(FootballPlayer::getId).collect(Collectors.toSet()));
        }
    }

    public static final String NAME_FIELD = "name";
    public static final String TRANSFER_COMMISSION_FIELD = "transfer_commission_percent";
    public static final String BALANCE_FIELD = "balance";
    public static final String TABLE_NAME = "football_team";
}
