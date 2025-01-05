package com.mixfa.football_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = FootballTeamRecord.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FootballTeamRecord {
    @Id
    @GeneratedValue
    private Long recordId;
    private long teamId;
    @Column(length = 128)
    private String name;
    private double transferCommissionPercent;
    private double balance;

    @Transient
    @JsonIgnore
    private transient FootballTeam team = null;

    public FootballTeamRecord(FootballTeam team) {
        this.recordId = null;
        this.teamId = team.getId();
        this.name = team.getName();
        this.transferCommissionPercent = team.getTransferCommissionPercent();
        this.balance = team.getBalance();
        this.team = team;
    }

    public static final String TABLE_NAME = "team_record";
    public static final String RECORD_ID_FIElD = "record_id";
    public static final String TEAM_ID_FIELD = "team_id";
}
