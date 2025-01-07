package com.mixfa.football_management.model.event;

import com.mixfa.football_management.model.FootballPlayer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

@Getter
@Accessors(fluent = true)
public class FootballPlayerUpdatedEvent extends ApplicationEvent {
    private final FootballPlayer player;

    public FootballPlayerUpdatedEvent(FootballPlayer player, Object source) {
        super(source);
        this.player = player;
    }
}
