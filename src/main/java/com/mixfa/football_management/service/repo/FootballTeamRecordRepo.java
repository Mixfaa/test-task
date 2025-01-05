package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballTeamRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballTeamRecordRepo extends JpaRepository<FootballTeamRecord, Long> {
}
