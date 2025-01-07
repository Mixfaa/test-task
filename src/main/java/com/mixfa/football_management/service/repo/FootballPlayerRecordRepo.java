package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FootballPlayerRecordRepo extends JpaRepository<FootballPlayerRecord, Long> {
    Optional<FootballPlayerRecord> findByRecordId(long recordId);

    Optional<FootballPlayerRecord> findByPlayerId(long playerId);
}
