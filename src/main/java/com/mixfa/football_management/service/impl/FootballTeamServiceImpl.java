package com.mixfa.football_management.service.impl;


import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.FootballTeamService;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballTeamServiceImpl implements FootballTeamService {
    private final FootballTeamRepo footballTeamRepo;
    private final FootballPlayerService footballPlayerService;

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
    @Transactional
    public void executeTransfer(FootballPlayerTransfer transfer) throws Exception {
        var teamFrom = transfer.teamFrom();
        var teamTo = transfer.teamTo();

        var teamFromBalance = teamFrom.balance() + transfer.teamFromReward();
        var teamToBalance = teamTo.balance() - transfer.playerPrice();

        teamFrom.balance(teamFromBalance);
        teamTo.balance(teamToBalance);

        footballPlayerService.moveToTeam(transfer.transferredPlayer(), teamTo);

        footballTeamRepo.save(teamFrom);
        footballTeamRepo.save(teamTo);
    }

    @Override
    public FootballTeam update(long id, FootballTeam team) throws Exception {
        return footballTeamRepo.save(team.id(id));
    }
}
