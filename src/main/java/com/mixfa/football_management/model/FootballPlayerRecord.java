package com.mixfa.football_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = FootballPlayerRecord.TABLE_NAME)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FootballPlayerRecord {
    @Id
    @GeneratedValue
    private Long recordId;

    private long playerId;
    @Column(length = 35)
    private String firstname;
    @Column(length = 35)
    private String lastname;
    private LocalDate dateOfBirth;
    private LocalDate careerBeginning;

    @Transient
    @JsonIgnore
    private transient FootballPlayer transferredPlayer = null;

    public FootballPlayerRecord(FootballPlayer player) {
        this.recordId = null;
        this.playerId = player.getId();
        this.firstname = player.getFirstname();
        this.lastname = player.getLastname();
        this.dateOfBirth = player.getDateOfBirth();
        this.careerBeginning = player.getCareerBeginning();
        this.transferredPlayer = player;
    }

    public static final String TABLE_NAME = "player_record";
    public static final String PLAYER_ID_FIELD = "player_id";
    public static final String RECORD_ID_FIELD = "record_id";
}
