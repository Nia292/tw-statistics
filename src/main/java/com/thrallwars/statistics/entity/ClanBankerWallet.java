package com.thrallwars.statistics.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClanBankerWallet {
    @Id
    private String id;
    private String ownerId;
    private String clanId;
    private Integer gold;
    private Integer silver;
    private Integer bronze;
    private Instant timestampUTC;
    private String server;
}
