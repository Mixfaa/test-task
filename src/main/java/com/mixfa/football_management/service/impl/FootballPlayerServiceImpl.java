package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballPlayerServiceImpl implements FootballPlayerService {
    private final FootballPlayerRepo footballPlayerRepo;
    private final FootballPlayerValidation footballPlayerValidation;

    @Override
    public FootballPlayer save(FootballPlayer.RegisterRequest registerRequest) throws Exception {
        var footballPlayer = new FootballPlayer(registerRequest);
        footballPlayerValidation.preSaveValidate(footballPlayer);
        return footballPlayerRepo.save(footballPlayer);
    }

    @Override
    public FootballPlayer update(long id, FootballPlayer footballPlayer) throws Exception {
        footballPlayer.setId(id); // not thread safe?? use KOTLIN!!!
        footballPlayerValidation.preSaveValidate(footballPlayer);
        return footballPlayerRepo.save(footballPlayer);
    }

    @Override
    public Optional<FootballPlayer> findById(long id) {
        return footballPlayerRepo.findById(id);
    }

    @Override
    public Page<FootballPlayer> list(LimitedPageable pageable) {
        return footballPlayerRepo.findAll(pageable);
    }

    @Override
    public void deleteById(long id) throws Exception {
        footballPlayerValidation.preDeleteValidate(id);
        footballPlayerRepo.deleteById(id);
    }
}
