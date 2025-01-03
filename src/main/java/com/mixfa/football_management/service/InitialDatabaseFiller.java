package com.mixfa.football_management.service;

import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitialDatabaseFiller implements CommandLineRunner {
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;
    private final FootballPlayerTransferService footballPlayerTransferService;
    private final FootballPlayerRepo footballPlayerRepo;
    private final FootballTeamRepo footballTeamRepo;

    private static final List<FootballPlayer.RegisterRequest> players = List.of(
            new FootballPlayer.RegisterRequest("Lionel", "Messi",
                    LocalDate.of(1987, 6, 24),
                    LocalDate.of(2004, 10, 16)),

            new FootballPlayer.RegisterRequest("Cristiano", "Ronaldo",
                    LocalDate.of(1985, 2, 5),
                    LocalDate.of(2002, 8, 14)),

            new FootballPlayer.RegisterRequest("Kylian", "Mbappé",
                    LocalDate.of(1998, 12, 20),
                    LocalDate.of(2015, 12, 2)),

            new FootballPlayer.RegisterRequest("Neymar", "Jr",
                    LocalDate.of(1992, 2, 5),
                    LocalDate.of(2009, 3, 7)),

            new FootballPlayer.RegisterRequest("Robert", "Lewandowski",
                    LocalDate.of(1988, 8, 21),
                    LocalDate.of(2005, 6, 17)),

            new FootballPlayer.RegisterRequest("Kevin", "De Bruyne",
                    LocalDate.of(1991, 6, 28),
                    LocalDate.of(2008, 9, 13)),

            new FootballPlayer.RegisterRequest("Mohamed", "Salah",
                    LocalDate.of(1992, 6, 15),
                    LocalDate.of(2010, 3, 3)),

            new FootballPlayer.RegisterRequest("Virgil", "van Dijk",
                    LocalDate.of(1991, 7, 8),
                    LocalDate.of(2009, 5, 9)),

            new FootballPlayer.RegisterRequest("Erling", "Haaland",
                    LocalDate.of(2000, 7, 21),
                    LocalDate.of(2016, 8, 12)),

            new FootballPlayer.RegisterRequest("Sadio", "Mané",
                    LocalDate.of(1992, 4, 10),
                    LocalDate.of(2011, 1, 15)),

            new FootballPlayer.RegisterRequest("Luka", "Modrić",
                    LocalDate.of(1985, 9, 9),
                    LocalDate.of(2003, 3, 1)),

            new FootballPlayer.RegisterRequest("Toni", "Kroos",
                    LocalDate.of(1990, 1, 4),
                    LocalDate.of(2007, 9, 26)),

            new FootballPlayer.RegisterRequest("Harry", "Kane",
                    LocalDate.of(1993, 7, 28),
                    LocalDate.of(2011, 8, 25)),

            new FootballPlayer.RegisterRequest("Eden", "Hazard",
                    LocalDate.of(1991, 1, 7),
                    LocalDate.of(2007, 11, 24)),

            new FootballPlayer.RegisterRequest("Karim", "Benzema",
                    LocalDate.of(1987, 12, 19),
                    LocalDate.of(2004, 1, 15))
    );

    private static final List<FootballTeam.RegisterRequest> teams = List.of(
            new FootballTeam.RegisterRequest("Barcelona IDC", 10.0, 25_000_000_000.0, Set.of()),
            new FootballTeam.RegisterRequest("Madrid Maybe", 5.0, 15_000_000_000.0, Set.of()),
            new FootballTeam.RegisterRequest("Kharkiv junkies", 3.0, 30_000_000_000.0, Set.of()),
            new FootballTeam.RegisterRequest("Lviv intelligence", 10.0, 10_000_000_000.0, Set.of())
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
