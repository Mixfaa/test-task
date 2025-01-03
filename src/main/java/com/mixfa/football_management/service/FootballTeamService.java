package com.mixfa.football_management.service;

import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballTeam;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FootballTeamService {
    FootballTeam save(FootballTeam.RegisterRequest registerRequest) throws Exception;

    Page<FootballTeam> list(LimitedPageable pageable);

    Optional<FootballTeam> findById(long id);

    void deleteById(long id) throws Exception;

    FootballTeam update(long id, FootballTeam.UpdateRequest updateRequest) throws Exception;
}
