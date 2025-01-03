package com.mixfa.football_management.controller;


import com.mixfa.football_management.exception.NotFoundException;
import com.mixfa.football_management.misc.LimitedPageable;
import com.mixfa.football_management.model.FootballTeam;
import com.mixfa.football_management.service.FootballTeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/footbalteams")
public class FootballTeamController {
    private final FootballTeamService footballTeamService;

    @PostMapping("/")
    public FootballTeam insert(@Valid @RequestBody FootballTeam.RegisterRequest registerRequest) throws Exception {
        return footballTeamService.save(registerRequest);
    }

    @GetMapping("/{id}")
    public FootballTeam findById(@PathVariable long id) throws Exception {
        return footballTeamService.findById(id)
                .orElseThrow(() -> NotFoundException.teamNotFound(id));
    }

    @GetMapping("/")
    public Page<FootballTeam> listTeams(int page, int pageSize) throws Exception {
        return footballTeamService.list(LimitedPageable.of(page, pageSize));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) throws Exception {
        footballTeamService.deleteById(id);
    }

    @PutMapping("/{id}")
    public FootballTeam update(@PathVariable long id, @RequestBody @Valid FootballTeam.UpdateRequest updateRequest) throws Exception {
        return footballTeamService.update(id, updateRequest);
    }
}
