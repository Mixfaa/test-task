package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.FootballPlayerTransferService;
import com.mixfa.football_management.service.FootballTeamService;
import com.mixfa.football_management.service.repo.FootballPlayerTransferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballPlayerTransferServiceImpl implements FootballPlayerTransferService {
    private final FootballPlayerTransferRepo footballPlayerTransferRepo;
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;

    @Override
    @Transactional
    public FootballPlayerTransfer makeTransfer(FootballPlayerTransfer.RegisterRequest registerRequest) throws Exception {
        var player = footballPlayerService.findById(registerRequest.playerId())
                .orElseThrow(() -> NotFoundException.playerNotFound(registerRequest.playerId()));

        if (player.currentTeam() == null)
            throw PlayerTransferException.orphanPlayer(player);

        var teamFrom = player.currentTeam();
        var teamTo = footballTeamService.findById(registerRequest.teamToId())
                .orElseThrow(() -> NotFoundException.teamNotFound(registerRequest.teamToId()));

        var playerTeamId = player.currentTeam().id();
        if (playerTeamId.equals(teamTo.id()))
            throw PlayerTransferException.playerAlreadyInTeam(player, teamTo);

        if (teamFrom.id().equals(teamTo.id()))
            throw PlayerTransferException.sameTeams(player, teamFrom);

        var transferPrice = FootballPlayerTransfer.calculatePlayerCost(player);

        var buyerTeamBalance = teamTo.balance();
        if (buyerTeamBalance < transferPrice)
            throw PlayerTransferException.teamCantAffordPlayer(player, teamTo);

        var fromTeamReward = FootballPlayerTransfer.calculateFromTeamReward(transferPrice, teamFrom.transferCommissionPercent());

        var transfer = new FootballPlayerTransfer(
                null,
                player,
                teamFrom,
                teamTo,
                transferPrice,
                teamFrom.transferCommissionPercent(),
                fromTeamReward,
                registerRequest.date()
        );

        transfer = footballPlayerTransferRepo.save(transfer);
        footballTeamService.executeTransfer(transfer);

        return transfer;
    }

    @Override
    public Page<FootballPlayerTransfer> list(LimitedPageable pageable) {
        return footballPlayerTransferRepo.findAll(pageable);
    }

    @Override
    public Optional<FootballPlayerTransfer> findById(long id) {
        return footballPlayerTransferRepo.findById(id);
    }

    @Override
    public void deleteById(long id) {
        footballPlayerTransferRepo.deleteById(id);
    }
}
