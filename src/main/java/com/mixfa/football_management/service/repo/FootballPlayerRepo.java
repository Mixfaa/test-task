package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FootballPlayerRepo extends JpaRepository<FootballPlayer, Long> {
    @Query("SELECT DISTINCT fp FROM FootballPlayer fp WHERE fp.id = :id AND (fp.currentTeam IS NULL OR fp.currentTeam.id = :teamId)")
    Optional<FootballPlayer> findByIdAndCurrentTeamIsNullOr(long id, long teamId);
    @Query("SELECT DISTINCT fp FROM FootballPlayer fp WHERE fp.id = :id AND fp.currentTeam IS NULL")
    Optional<FootballPlayer> findByIdAndCurrentTeamIsNull(long id);
}
