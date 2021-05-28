package com.thrallwars.statistics.entity;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class OnlinePlayers {
    private OffsetDateTime time;
    private List<OnlinePlayer> players;
}
