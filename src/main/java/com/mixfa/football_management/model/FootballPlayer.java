package com.mixfa.football_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = FootballPlayer.TABLE_NAME)
public class FootballPlayer {
    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 35)
    private String firstname;
    @Column(length = 35)
    private String lastname;
    @ManyToOne(optional = true)
    private FootballTeam currentTeam;
    private LocalDate dateOfBirth;
    private LocalDate careerBeginning;

    public FootballPlayer(RegisterRequest registerRequest) {
        this(null, registerRequest.firstname, registerRequest.lastname, null, registerRequest.dateOfBirth, registerRequest.careerBeginning);
    }

    public record RegisterRequest(@NotBlank String firstname, @NotBlank String lastname,
                                  @Past @NotNull LocalDate dateOfBirth,
                                  @Past @NotNull LocalDate careerBeginning) {
    }

    public static final String FIRSTNAME_FIELD = "firstname";
    public static final String LASTNAME_FIELD = "lastname";
    public static final String CURRENT_TEAM_ID_FIELD = "current_team_id";
    public static final String DATE_OF_BIRTH_FIELD = "date_of_birth";
    public static final String CAREER_BEGINNING_FIELD = "career_beginning";
    public static final String TABLE_NAME = "football_player";
}
