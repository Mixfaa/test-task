package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballPlayerRepo extends JpaRepository<FootballPlayer, Long> {
}
