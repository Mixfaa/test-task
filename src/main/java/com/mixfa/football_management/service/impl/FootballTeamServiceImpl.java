package com.mixfa.football_management.service.impl;


import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballTeamService;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballTeamServiceImpl implements FootballTeamService {
    private final FootballTeamRepo footballTeamRepo;

    @Override
    public FootballTeam save(FootballTeam.RegisterRequest registerRequest) {
        var footballTeam = new FootballTeam(registerRequest);
        return footballTeamRepo.save(footballTeam);
    }

    @Override
    public Page<FootballTeam> list(LimitedPageable pageable) {
        return footballTeamRepo.findAll(pageable);
    }

    @Override
    public Optional<FootballTeam> findById(long id) {
        return footballTeamRepo.findById(id);
    }

    @Override
    public void deleteById(long id) {
        footballTeamRepo.deleteById(id);
    }

    @Override
    public FootballTeam update(long id, FootballTeam team) throws Exception {
        return footballTeamRepo.save(team.id(id));
    }
}
