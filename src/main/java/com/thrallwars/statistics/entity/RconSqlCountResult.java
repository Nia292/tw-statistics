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
public class RconSqlCountResult {
    @RconSqlColumn(columnIndex = 0)
    private String count;
}
