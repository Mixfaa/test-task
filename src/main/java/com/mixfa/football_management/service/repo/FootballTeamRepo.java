package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FootballTeamRepo extends JpaRepository<FootballTeam, Long> {
    boolean existsByIdAndPlayersIsEmpty(long id);

    @Query("SELECT COUNT(DISTINCT t) FROM FootballTeam t JOIN t.players p WHERE p.id = :playerId")
    long findAllByPlayersContains(long playerId);
}
