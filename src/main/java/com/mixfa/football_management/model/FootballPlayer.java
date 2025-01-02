package com.mixfa.football_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Entity
@Accessors(fluent = true)
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
    private LocalDateTime dateOfBirth;
    private LocalDateTime careerBeginning;

    public FootballPlayer(RegisterRequest registerRequest) {
        this(null, registerRequest.firstname, registerRequest.lastname, null, registerRequest.dateOfBirth, registerRequest.careerBeginning);
    }

    public record RegisterRequest(@NotBlank String firstname, @NotBlank String lastname,
                                  @Past @NotNull LocalDateTime dateOfBirth,
                                  @Past @NotNull LocalDateTime careerBeginning) {
    }

    public static final String TABLE_NAME = "football_player";
}