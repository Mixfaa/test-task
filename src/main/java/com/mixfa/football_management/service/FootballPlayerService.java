package com.mixfa.football_management.service;

import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayer;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FootballPlayerService {
    FootballPlayer save(FootballPlayer.RegisterRequest registerRequest) throws Exception;

    FootballPlayer update(long id, FootballPlayer.UpdateRequest updateRequest) throws Exception;

    Optional<FootballPlayer> findById(long id);

    Optional<FootballPlayer> findOrphanOrIsIn(long id, long teamId);

    Optional<FootballPlayer> findOrphan(long id);

    Page<FootballPlayer> list(LimitedPageable pageable);

    void deleteById(long id) throws Exception;
}
