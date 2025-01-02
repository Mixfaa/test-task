package com.mixfa.football_management.controller;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayerTransfer;
import com.mixfa.football_management.service.FootballPlayerTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/footballplayertransfers")
public class FootballPlayerTransferController {
    private final FootballPlayerTransferService footballPlayerTransferService;

    @PostMapping("/")
    public FootballPlayerTransfer makeTransfer(FootballPlayerTransfer.RegisterRequest registerRequest) throws Exception {
        return footballPlayerTransferService.makeTransfer(registerRequest);
    }

    @GetMapping("/{id}")
    public FootballPlayerTransfer findById(@PathVariable long id) throws Exception {
        return footballPlayerTransferService.findById(id)
                .orElseThrow(() -> NotFoundException.transferNotFound(id));
    }

    @GetMapping("/")
    public Page<FootballPlayerTransfer> listTransfers(int page, int pageSize) throws Exception {
        return footballPlayerTransferService.list(LimitedPageable.of(page, pageSize));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) throws Exception {
        footballPlayerTransferService.deleteById(id);
    }
}
