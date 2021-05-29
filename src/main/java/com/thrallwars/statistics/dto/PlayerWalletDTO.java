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
public class PlayerWalletDTO {
    @RconSqlColumn(columnIndex = 1)
    private String charName;
    @RconSqlColumn(columnIndex = 2)
    private String clanId;
    @RconSqlColumn(columnIndex = 3)
    private String goldHex;
    @RconSqlColumn(columnIndex = 4)
    private String silverHex;
    @RconSqlColumn(columnIndex = 5)
    private String bronzeHex;
}
