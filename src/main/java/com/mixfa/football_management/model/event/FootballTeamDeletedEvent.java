package com.mixfa.football_management.model.event;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

@Getter
@Accessors(fluent = true)
public class FootballTeamDeletedEvent extends ApplicationEvent {
    private final long teamId;

    public FootballTeamDeletedEvent(long teamId, Object source) {
        super(source);
        this.teamId = teamId;
    }
}
