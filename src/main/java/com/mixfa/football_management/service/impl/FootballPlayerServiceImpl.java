package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class FootballPlayerServiceImpl implements FootballPlayerService {
    private final FootballPlayerRepo footballPlayerRepo;

    @Override
    public FootballPlayer save(@Valid FootballPlayer.RegisterRequest registerRequest) {
        var footballPlayer = new FootballPlayer(registerRequest);
        return footballPlayerRepo.save(footballPlayer);
    }

    @Override
    public FootballPlayer update(long id, FootballPlayer footballPlayer) throws Exception {
        return footballPlayerRepo.save(footballPlayer.id(id));
    }

    @Override
    public FootballPlayer moveToTeam(FootballPlayer footballPlayer, FootballTeam team) {
        return footballPlayerRepo.save(footballPlayer.currentTeam(team));
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
    public void deleteById(long id) {
        footballPlayerRepo.deleteById(id);
    }
}
