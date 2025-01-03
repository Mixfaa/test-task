package com.mixfa.football_management.service.impl;


import com.mixfa.football_management.exception.NotFoundException;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballTeamServiceImpl implements FootballTeamService {
    private final FootballTeamRepo footballTeamRepo;
    private final FootballPlayerService footballPlayerService;
    private final FootballTeamValidation footballTeamValidation;

    private Set<FootballPlayer> findOrphanPlayersByIds(Set<Long> playerIds, Long teamId) throws Exception {
        try {
            return playerIds == null ? Set.of() : playerIds.stream().map(id -> {
                try {
                    if (teamId == null)
                        return footballPlayerService.findOrphan(id).orElseThrow(() -> NotFoundException.playerNotFound(id));
                    else
                        return footballPlayerService.findOrphanOrIsIn(id, teamId).orElseThrow(() -> NotFoundException.playerNotFound(id));
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toSet());
        } catch (RuntimeException e) {
            throw Utils.unwrapRuntimeException(e);
        }
    }

    private Set<FootballPlayer> findOrphanPlayersByIds(Set<Long> playerIds) throws Exception {
        return findOrphanPlayersByIds(playerIds, null);
    }

    @Override
    @Transactional
    public FootballTeam save(FootballTeam.RegisterRequest registerRequest) throws Exception {
        var players = findOrphanPlayersByIds(registerRequest.playerIds());
        var footballTeam = new FootballTeam(registerRequest);
        footballTeam.addPlayers(players);
        footballTeam = footballTeamRepo.save(footballTeam);

        footballTeamValidation.onSaveValidate(footballTeam);
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
    @Transactional
    public FootballTeam update(long id, FootballTeam.UpdateRequest updateRequest) throws Exception {
        var players = findOrphanPlayersByIds(updateRequest.playerIds(), id);
        var footballTeam = new FootballTeam(id, updateRequest.name(), updateRequest.transferCommission(), updateRequest.balance());
        footballTeam.addPlayers(players);
        footballTeam = footballTeamRepo.save(footballTeam);
        footballTeamValidation.onSaveValidate(footballTeam);
        return footballTeam;
    }
}
