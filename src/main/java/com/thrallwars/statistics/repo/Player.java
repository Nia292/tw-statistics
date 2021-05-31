package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.util.rconsql.RconSqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;
import java.time.Instant;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    private String id;
    private String playerId;
    private String charName;
    private String level;
    private String clanId;
    /**
     * Epoch timestamp, measured in seconds.
     */
    private String lastTimeOnlineTS;
    private String clanName;
    private Instant timestampUTC;
    private String server;
}
