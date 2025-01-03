package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballTeamRepo extends JpaRepository<FootballTeam, Long> {
//    long countAllByPlayersContains(FootballPlayer player);

    @Query(
            "select count(*) from FootballTeam team join FootballPlayer fp on team.id = fp.currentTeam.id and fp.id = :playerId"
    )
    long countAllByPlayersContains(long playerId);
}
