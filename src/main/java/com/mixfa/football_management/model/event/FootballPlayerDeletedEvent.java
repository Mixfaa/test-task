package com.mixfa.football_management.model.event;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

@Getter
@Accessors(fluent = true)
public class FootballPlayerDeletedEvent extends ApplicationEvent {
    private final long playerId;

    public FootballPlayerDeletedEvent(long playerId, Object source) {
        super(source);
        this.playerId = playerId;
    }
}
