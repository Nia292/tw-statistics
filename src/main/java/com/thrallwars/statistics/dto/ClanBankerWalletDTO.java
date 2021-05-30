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
public class ClanBankerWalletDTO {

    @RconSqlColumn(columnIndex = 0)
    private String ownerId;
    @RconSqlColumn(columnIndex = 1)
    private String guildId;
    @RconSqlColumn(columnIndex = 2)
    private String goldHex;
    @RconSqlColumn(columnIndex = 3)
    private String silverHex;
    @RconSqlColumn(columnIndex = 4)
    private String bronzeHey;
}
