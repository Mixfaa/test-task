package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerTransferValidation;
import com.mixfa.football_management.model.*;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.FootballPlayerTransferService;
import com.mixfa.football_management.service.FootballTeamService;
import com.mixfa.football_management.service.repo.FootballPlayerRecordRepo;
import com.mixfa.football_management.service.repo.FootballPlayerTransferRepo;
import com.mixfa.football_management.service.repo.FootballTeamRecordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballPlayerTransferServiceImpl implements FootballPlayerTransferService {
    private final FootballPlayerTransferRepo footballPlayerTransferRepo;
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamService footballTeamService;

    private final FootballPlayerRecordRepo playerRecordRepo;
    private final FootballTeamRecordRepo teamRecordRepo;
    private final FootballPlayerTransferValidation footballPlayerTransferValidation;

    private void finalizeTransfer(FootballPlayerTransfer transfer) throws Exception {
        var teamFrom = transfer.getTeamFromRecord();
        var teamTo = transfer.getTeamToRecord();

        var teamFromBalance = teamFrom.getBalance() + transfer.getTeamFromReward();
        var teamToBalance = teamTo.getBalance() - (transfer.getPlayerPrice() + transfer.getTeamFromReward());

        teamFrom.setBalance(teamFromBalance);
        teamTo.setBalance(teamToBalance);

        Objects.requireNonNull(transfer.getPlayerRecord().getTransferredPlayer());
        Objects.requireNonNull(teamTo.getTeam());
        moveToTeamNoTx(transfer.getPlayerRecord().getTransferredPlayer(), teamTo.getTeam());
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
                .playerRecord(
                        playerRecordRepo.save(new FootballPlayerRecord(player))
                )
                .teamFromRecord(
                        teamRecordRepo.save(new FootballTeamRecord(teamFrom))
                )
                .teamToRecord(
                        teamRecordRepo.save(new FootballTeamRecord(teamTo))
                )
                .playerPrice(playerPrice)
                .teamFromCommission(teamFrom.getTransferCommissionPercent())
                .teamFromReward(teamFromReward)
                .date(registerRequest.date())
                .build();
        footballPlayerTransferValidation.onSaveValidate(transfer);
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
        team.addPlayer(player);
        footballTeamService.update(team.getId(), new FootballTeam.UpdateRequest(team));
    }

    @Override
    @Transactional
    public void moveToTeam(FootballPlayer player, FootballTeam team) throws Exception {
        moveToTeamNoTx(player, team);
    }
}
