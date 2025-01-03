package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerTransferValidation;
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

    private final FootballPlayerTransferValidation footballPlayerTransferValidation;

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

        var playerPrice = FootballPlayerTransfer.calculatePlayerCost(player);
        var teamFromReward = FootballPlayerTransfer.calculateFromTeamReward(playerPrice, teamFrom.getTransferCommissionPercent());

        var transfer = FootballPlayerTransfer.builder()
                .transferredPlayer(player)
                .teamFrom(teamFrom)
                .teamTo(teamTo)
                .playerPrice(playerPrice)
                .teamFromCommission(teamFrom.getTransferCommissionPercent())
                .teamFromReward(teamFromReward)
                .date(registerRequest.date())
                .build();
        footballPlayerTransferValidation.preSaveValidate(transfer);
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
    public void deleteById(long id) throws Exception {
        footballPlayerTransferValidation.preDeleteValidate(id);
        footballPlayerTransferRepo.deleteById(id);
    }

    private void moveToTeamNoTx(FootballPlayer player, FootballTeam team) throws Exception {
        team.getPlayers().add(player);
        team = footballTeamService.update(team.getId(), team);
        player.setCurrentTeam(team);
        footballPlayerService.update(player.getId(), player);
    }

    @Override
    @Transactional
    public void moveToTeam(FootballPlayer player, FootballTeam team) throws Exception {
        moveToTeamNoTx(player, team);
    }
}
