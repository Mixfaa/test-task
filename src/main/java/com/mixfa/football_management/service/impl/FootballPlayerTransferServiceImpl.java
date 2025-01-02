package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import com.mixfa.football_management.model.FootballTeam;
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

    private void finalizeTransfer(FootballPlayerTransfer transfer) throws Exception {
        var teamFrom = transfer.getTeamFrom();
        var teamTo = transfer.getTeamTo();

        var teamFromBalance = teamFrom.getBalance() + transfer.getTeamFromReward();
        var teamToBalance = teamTo.getBalance() - (transfer.getPlayerPrice() + transfer.getTeamFromReward());

        teamFrom.setBalance(teamFromBalance);
        teamTo.setBalance(teamToBalance);

        moveToTeamNoTx(transfer.getTransferredPlayer(), teamTo);
        footballTeamService.update(teamFrom.getId(), teamFrom);
    }

    @Override
    @Transactional
    public FootballPlayerTransfer makeTransfer(FootballPlayerTransfer.RegisterRequest registerRequest) throws Exception {
        var player = footballPlayerService.findById(registerRequest.playerId())
                .orElseThrow(() -> NotFoundException.playerNotFound(registerRequest.playerId()));

        if (player.getCurrentTeam() == null)
            throw PlayerTransferException.orphanPlayer(player);

        var teamFrom = player.getCurrentTeam();
        var teamTo = footballTeamService.findById(registerRequest.teamToId())
                .orElseThrow(() -> NotFoundException.teamNotFound(registerRequest.teamToId()));

        var playerTeamId = player.getCurrentTeam().getId();
        if (playerTeamId.equals(teamTo.getId()))
            throw PlayerTransferException.playerAlreadyInTeam(player, teamTo);

        if (teamFrom.getId().equals(teamTo.getId()))
            throw PlayerTransferException.sameTeams(player, teamFrom);

        var playerPrice = FootballPlayerTransfer.calculatePlayerCost(player);
        var teamFromReward = FootballPlayerTransfer.calculateFromTeamReward(playerPrice, teamFrom.getTransferCommissionPercent());
        var totalTransferPrice = playerPrice + teamFromReward;

        var buyerTeamBalance = teamTo.getBalance();
        if (buyerTeamBalance < totalTransferPrice)
            throw PlayerTransferException.teamCantAffordPlayer(player, teamTo);

        var transfer = FootballPlayerTransfer.builder()
                .transferredPlayer(player)
                .teamFrom(teamFrom)
                .teamTo(teamTo)
                .playerPrice(playerPrice)
                .teamFromCommission(teamFrom.getTransferCommissionPercent())
                .teamFromReward(teamFromReward)
                .date(registerRequest.date())
                .build();

        transfer = footballPlayerTransferRepo.save(transfer);
        finalizeTransfer(transfer);

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

    private void moveToTeamNoTx(FootballPlayer player, FootballTeam team) throws Exception {
        team.getPlayers().add(player);
        footballPlayerService.update(player.getId(), player);
        footballTeamService.update(team.getId(), team);
    }

    @Override
    @Transactional
    public void moveToTeam(FootballPlayer player, FootballTeam team) throws Exception {
        moveToTeamNoTx(player, team);
    }
}
