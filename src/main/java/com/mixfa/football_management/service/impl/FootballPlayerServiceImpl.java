package com.mixfa.football_management.service.impl;

import com.mixfa.football_management.exception.ValidationException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.misc.dbvalidation.FootballPlayerValidation;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.service.FootballPlayerService;
import com.mixfa.football_management.service.repo.FootballPlayerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballPlayerServiceImpl implements FootballPlayerService {
    private final FootballPlayerRepo footballPlayerRepo;

    private void validatePlayerParams(
            LocalDateTime dateOfBirth,
            LocalDateTime careerBeginning
    ) throws Exception {
        var currentTime = LocalDateTime.now();

        if (dateOfBirth.isAfter(currentTime) || dateOfBirth.isEqual(currentTime))
            throw new ValidationException(FootballPlayerValidation.MSG_DATE_OF_BIRTH_MUST_BE_IN_PAST);

        if (careerBeginning.isBefore(dateOfBirth) ||
                careerBeginning.isEqual(dateOfBirth))
            throw new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_AFTER_BIRTH_DATE);

        if (careerBeginning.isAfter(currentTime) || careerBeginning.isEqual(currentTime))
            throw new ValidationException(FootballPlayerValidation.MSG_CAREER_BEGINNING_MUST_BE_IN_PAST);
    }

    @Override
    public FootballPlayer save(FootballPlayer.RegisterRequest registerRequest) throws Exception {
        validatePlayerParams(registerRequest.dateOfBirth(), registerRequest.careerBeginning());

        var footballPlayer = new FootballPlayer(registerRequest);
        return footballPlayerRepo.save(footballPlayer);
    }

    @Override
    public FootballPlayer update(long id, FootballPlayer footballPlayer) throws Exception {
        validatePlayerParams(footballPlayer.getDateOfBirth(), footballPlayer.getCareerBeginning());
        footballPlayer.setId(id); // not thread safe?? use KOTLIN!!!
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
    public void deleteById(long id) {
        footballPlayerRepo.deleteById(id);
    }
}
