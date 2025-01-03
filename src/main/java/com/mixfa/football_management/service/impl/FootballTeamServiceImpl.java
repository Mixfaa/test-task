package com.mixfa.football_management.service.impl;


import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.exception.PlayerTransferException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.Utils;
import com.mixfa.football_management.misc.dbvalidation.FootballTeamValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.FootballTeamService;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballTeamServiceImpl implements FootballTeamService {
    private final FootballTeamRepo footballTeamRepo;
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamValidation footballTeamValidation;

    private Set<FootballPlayer> findOrphanPlayersByIds(Set<Long> playerIds) throws Exception {
        try {
            return playerIds == null ? Set.of() : playerIds.stream().map(id -> {
                try {
                    var player = footballPlayerService.findById(id).orElseThrow(() -> NotFoundException.playerNotFound(id));
                    if (player.getCurrentTeam() != null)
                        throw new RuntimeException(PlayerTransferException.playerAlreadyInTeam(player));
                    return player;
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw Utils.unwrapRuntimeException(e);
        }
    }

    private void movePlayersToTeam(Collection<FootballPlayer> players, FootballTeam team) throws Exception {
        for (FootballPlayer player : players) {
            player.setCurrentTeam(team);
            footballPlayerService.update(player.getId(), player);
        }
    }

    @Override
    @Transactional
    public FootballTeam save(FootballTeam.RegisterRequest registerRequest) throws Exception {
        var players = findOrphanPlayersByIds(registerRequest.playerIds());
        var footballTeam = footballTeamRepo.save(new FootballTeam(registerRequest, players));
        movePlayersToTeam(players, footballTeam);
        footballTeamValidation.preSaveValidate(footballTeam);
        return footballTeam;
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
    public void deleteById(long id) throws Exception {
        footballTeamValidation.preDeleteValidate(id);
        footballTeamRepo.deleteById(id);
    }

    @Override
    public FootballTeam update(long id, FootballTeam team) throws Exception {
        team.setId(id);
        footballTeamValidation.preSaveValidate(team);
        return footballTeamRepo.save(team);
    }

    @Override
    @Transactional
    public FootballTeam update(long id, FootballTeam.UpdateRequest updateRequest) throws Exception {
        var players = findOrphanPlayersByIds(updateRequest.playerIds());
        var team = footballTeamRepo.save(
                new FootballTeam(id, updateRequest.name(), updateRequest.transferCommission(), updateRequest.balance(), players)
        );

        movePlayersToTeam(players, team);
        footballTeamValidation.preSaveValidate(team);
        return team;
    }
}
