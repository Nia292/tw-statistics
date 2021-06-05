package com.thrallwars.statistics.entity;

import com.thrallwars.statistics.util.rconsql.RconSqlColumn;
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
public class OnlinePlayer {
    @Id
    private String id;
    @RconSqlColumn(columnIndex = 1)
    private String characterName;
    @RconSqlColumn(columnIndex = 2)
    private String accountName;
    @RconSqlColumn(columnIndex = 4)
    private String steamId;
    private Instant timestampUTC;
}
