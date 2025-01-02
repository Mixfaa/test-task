package com.mixfa.football_management.service;

import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface FootballPlayerTransferService {
    FootballPlayerTransfer makeTransfer(FootballPlayerTransfer.RegisterRequest registerRequest) throws Exception;

    Page<FootballPlayerTransfer> list(LimitedPageable pageable);

    Optional<FootballPlayerTransfer> findById(long id);

    void deleteById(long id)  throws Exception;
}
