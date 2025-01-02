package com.mixfa.football_management.model;

import com.mixfa.football_management.misc.dbvalidation.FootballTeamValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Check;

import java.util.HashSet;
import java.util.Set;

@Entity
@Accessors(fluent = true)
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

    @Check(name = FootballTeamValidation.ID_COMMISSION_BOUNDS, constraints = "transfer_commission_percent >= 0 AND transfer_commission_percent <= 10")
    private double transferCommissionPercent; // 0 - 10%
    @Check(name = FootballTeamValidation.ID_BALANCE_BOUND, constraints = "balance >= 0")
    private double balance;

    @OneToMany
    private Set<FootballPlayer> players;

    public FootballTeam(RegisterRequest registerRequest) {
        this(
                null,
                registerRequest.name,
                registerRequest.transferCommission,
                registerRequest.balance,
                new HashSet<>()
        );
    }

    public record RegisterRequest(
            @NotBlank
            String name,
            @NotNull
            @Min(value = 0)
            @Max(value = 10)
            double transferCommission,
            double balance
    ) {
    }


    public static final String TABLE_NAME = "football_team";
}
