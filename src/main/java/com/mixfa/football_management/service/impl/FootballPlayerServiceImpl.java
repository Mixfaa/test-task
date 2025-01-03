package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import com.mixfa.football_management.service.repo.FootballTeamRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballPlayerServiceImpl implements FootballPlayerService {
    private final FootballPlayerRepo footballPlayerRepo;
    private final FootballTeamRepo footballTeamRepo;
    private final FootballPlayerValidation footballPlayerValidation;

    @Override
    public FootballPlayer save(FootballPlayer.RegisterRequest registerRequest) throws Exception {
        var footballPlayer = new FootballPlayer(registerRequest);
        footballPlayerValidation.onSaveValidate(footballPlayer);
        return footballPlayerRepo.save(footballPlayer);
    }

    @Override
    @Transactional
    public FootballPlayer update(long id, FootballPlayer.UpdateRequest updateRequest) throws Exception {
        var teamId = updateRequest.teamId();
        FootballTeam team;
        if (teamId == null)
            team = null;
        else {
            team = footballTeamRepo.findById(teamId).orElseThrow(
                    () -> NotFoundException.teamNotFound(teamId)
            );
        }
        var player = new FootballPlayer(
                id,
                updateRequest.firstname(),
                updateRequest.lastname(),
                team,
                updateRequest.dateOfBirth(),
                updateRequest.careerBeginning()
        );
        if (team != null) {
            team.addPlayer(player);
            footballTeamRepo.save(team);
        }

        player = footballPlayerRepo.save(player);
        footballPlayerValidation.onSaveValidate(player);
        return player;
    }

    @Override
    public Optional<FootballPlayer> findById(long id) {
        return footballPlayerRepo.findById(id);
    }

    @Override
    public Optional<FootballPlayer> findOrphanOrIsIn(long id, long teamId) {
        return footballPlayerRepo.findByIdAndCurrentTeamIsNullOr(id, teamId);
    }

    @Override
    public Optional<FootballPlayer> findOrphan(long id) {
        return footballPlayerRepo.findByIdAndCurrentTeamIsNull(id);
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
