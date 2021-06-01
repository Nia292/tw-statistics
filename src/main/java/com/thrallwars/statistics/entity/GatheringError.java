package com.thrallwars.statistics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatheringError {
    private String id;
    private String operation;
    private Instant timestampUTC;
}
