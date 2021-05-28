package com.thrallwars.statistics.entity;

import com.thrallwars.statistics.util.rconsql.RconSqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlinePlayer {
    @RconSqlColumn(columnIndex = 1)
    private String characterName;
    @RconSqlColumn(columnIndex = 2)
    private String accountName;
    @RconSqlColumn(columnIndex = 4)
    private String steamId;
}
