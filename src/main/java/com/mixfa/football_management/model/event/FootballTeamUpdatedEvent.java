package com.mixfa.football_management.model.event;

import com.mixfa.football_management.model.FootballTeam;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

@Getter
@Accessors(fluent = true)
public class FootballTeamUpdatedEvent extends ApplicationEvent {
    private final FootballTeam team;

    public FootballTeamUpdatedEvent(FootballTeam team, Object source) {
        super(source);
        this.team = team;
    }
}
