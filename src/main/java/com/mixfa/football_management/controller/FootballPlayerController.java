package com.mixfa.football_management.controller;

import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballPlayer;
import com.mixfa.football_management.service.FootballPlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/footballplayers")
public class FootballPlayerController {
    private final FootballPlayerService footballPlayerService;

    @PostMapping("/")
    public FootballPlayer insertPlayer(@Valid @RequestBody FootballPlayer.RegisterRequest registerRequest) throws Exception {
        return footballPlayerService.save(registerRequest);
    }

    @GetMapping("/{id}")
    public FootballPlayer findById(@PathVariable long id) throws Exception {
        return footballPlayerService.findById(id)
                .orElseThrow(() -> NotFoundException.playerNotFound(id));
    }

    @GetMapping("/")
    public Page<FootballPlayer> listPlayers(int page, int pageSize) throws Exception {
        return footballPlayerService.list(LimitedPageable.of(page, pageSize));
    }

    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable long id) throws Exception {
        footballPlayerService.deleteById(id);
    }

    @PutMapping("/{id}")
    public FootballPlayer update(@PathVariable long id, FootballPlayer footballPlayer) throws Exception {
        return footballPlayerService.update(id, footballPlayer);
    }
}
