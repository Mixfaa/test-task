package com.mixfa.football_management.service.repo;

import com.mixfa.football_management.model.FootballPlayerTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballPlayerTransferRepo extends JpaRepository<FootballPlayerTransfer, Long> {
}
