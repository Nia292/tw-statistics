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
public class PlayerWallet {
    /**
     * Unique record ID assigned by mongo
     */
    @Id
    private String id;
    /**
     * Clan ID simply taken from the player table
     */
    private String clanId;
    /**
     * char_name from the player table
     */
    private String charName;
    /**
     * Converted gold value
     */
    private Integer gold;
    /**
     * Hexadecimal gold string
     */
    private String rawGoldValue;
    /**
     * Converted silver value
     */
    private Integer silver;

    private String rawSilverValue;
    /**
     * Converted bronze value
     */
    private Integer bronze;
    private String rawBronzeValue;
    /**
     * the server this information was taken from, as specified in {@link com.thrallwars.statistics.config.RconTarget#name}
     */
    private String server;
    /**
     * Timestamp this record was taken on. In UTC time.
     */
    private Instant timestampUTC;
}
