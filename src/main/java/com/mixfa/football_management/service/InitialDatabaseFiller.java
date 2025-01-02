package com.mixfa.football_management.service;

import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialDatabaseFiller implements CommandLineRunner {
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;
    private final FootballPlayerTransferService footballPlayerTransferService;

    private static final List<FootballPlayer.RegisterRequest> players = List.of(
            new FootballPlayer.RegisterRequest("Lionel", "Messi",
                    LocalDateTime.of(1987, 6, 24, 0, 0),
                    LocalDateTime.of(2004, 10, 16, 0, 0)),

            new FootballPlayer.RegisterRequest("Cristiano", "Ronaldo",
                    LocalDateTime.of(1985, 2, 5, 0, 0),
                    LocalDateTime.of(2002, 8, 14, 0, 0)),

            new FootballPlayer.RegisterRequest("Kylian", "Mbappé",
                    LocalDateTime.of(1998, 12, 20, 0, 0),
                    LocalDateTime.of(2015, 12, 2, 0, 0)),

            new FootballPlayer.RegisterRequest("Neymar", "Jr",
                    LocalDateTime.of(1992, 2, 5, 0, 0),
                    LocalDateTime.of(2009, 3, 7, 0, 0)),

            new FootballPlayer.RegisterRequest("Robert", "Lewandowski",
                    LocalDateTime.of(1988, 8, 21, 0, 0),
                    LocalDateTime.of(2005, 6, 17, 0, 0)),

            new FootballPlayer.RegisterRequest("Kevin", "De Bruyne",
                    LocalDateTime.of(1991, 6, 28, 0, 0),
                    LocalDateTime.of(2008, 9, 13, 0, 0)),

            new FootballPlayer.RegisterRequest("Mohamed", "Salah",
                    LocalDateTime.of(1992, 6, 15, 0, 0),
                    LocalDateTime.of(2010, 3, 3, 0, 0)),

            new FootballPlayer.RegisterRequest("Virgil", "van Dijk",
                    LocalDateTime.of(1991, 7, 8, 0, 0),
                    LocalDateTime.of(2009, 5, 9, 0, 0)),

            new FootballPlayer.RegisterRequest("Erling", "Haaland",
                    LocalDateTime.of(2000, 7, 21, 0, 0),
                    LocalDateTime.of(2016, 8, 12, 0, 0)),

            new FootballPlayer.RegisterRequest("Sadio", "Mané",
                    LocalDateTime.of(1992, 4, 10, 0, 0),
                    LocalDateTime.of(2011, 1, 15, 0, 0)),

            new FootballPlayer.RegisterRequest("Luka", "Modrić",
                    LocalDateTime.of(1985, 9, 9, 0, 0),
                    LocalDateTime.of(2003, 3, 1, 0, 0)),

            new FootballPlayer.RegisterRequest("Toni", "Kroos",
                    LocalDateTime.of(1990, 1, 4, 0, 0),
                    LocalDateTime.of(2007, 9, 26, 0, 0)),

            new FootballPlayer.RegisterRequest("Harry", "Kane",
                    LocalDateTime.of(1993, 7, 28, 0, 0),
                    LocalDateTime.of(2011, 8, 25, 0, 0)),

            new FootballPlayer.RegisterRequest("Eden", "Hazard",
                    LocalDateTime.of(1991, 1, 7, 0, 0),
                    LocalDateTime.of(2007, 11, 24, 0, 0)),

            new FootballPlayer.RegisterRequest("Karim", "Benzema",
                    LocalDateTime.of(1987, 12, 19, 0, 0),
                    LocalDateTime.of(2004, 1, 15, 0, 0))
    );

    private static final List<FootballTeam.RegisterRequest> teams = List.of(
            new FootballTeam.RegisterRequest("Barcelona IDC", 10.0, 25_000_000_000.0),
            new FootballTeam.RegisterRequest("Madrid Maybe", 5.0, 15_000_000_000.0),
            new FootballTeam.RegisterRequest("Kharkiv junkies", 3.0, 30_000_000_000.0),
            new FootballTeam.RegisterRequest("Lviv intelligence", 10.0, 10_000_000_000.0)
    );

    private static final Random random = new Random();

    private static <T> T takeRandom(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var existsFile = new File("dbinit");
        if (existsFile.exists()) return;

        var footballTeams = teams.stream().map(team -> {
            try {
                return footballTeamService.save(team);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
        var footballPlayers = new ArrayList<>(players.stream().map(player -> {
            try {
                return footballPlayerService.save(player);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList());

        Collections.shuffle(footballPlayers);
        footballPlayers.stream().limit(7).forEach(player -> {
            try {
                footballPlayerTransferService.moveToTeam(player, takeRandom(footballTeams));
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
            }
        });

        log.info("Database filled");
        existsFile.createNewFile();
    }
}
