package com.thrallwars.statistics.dto;

import com.thrallwars.statistics.util.rconsql.RconSqlColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO {
    @RconSqlColumn(columnIndex = 0)
    private String playerId;
    @RconSqlColumn(columnIndex = 1)
    private String charName;
    @RconSqlColumn(columnIndex = 2)
    private String level;
    @RconSqlColumn(columnIndex = 3)
    private String clanId;
    @RconSqlColumn(columnIndex = 4)
    private String lastTimeOnlineTS;
    @RconSqlColumn(columnIndex = 5)
    private String clanName;
}
