package com.thrallwars.statistics.service;

import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.entity.RconSqlCountResult;
import com.thrallwars.statistics.util.StatisticsUtils;
import com.thrallwars.statistics.util.rcon.RconSocket;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlayerWalletService {

    /**
     * Limitation of RCON. We need to limit result sets
     */
    private static final Integer MAX_ROWS_PER_RESULT = 100;

    // What's up with these hex(substr()) operations?
    // a property is, in essence, a serialized game object
    // Through a complex process (try and error...), I found out which bytes to read to get
    // the integer values from the blob
    // To transmit them over rcon, they are converted into a hex string.

    public List<PlayerWallet> queryWallets(RconSocket rconSocket) {
        int size = queryCount(rconSocket);
        List<PlayerWallet> result = new ArrayList<>();
        for (int currentOffset = 0; currentOffset <= size; currentOffset += 100) {
            Collection<PlayerWallet> playerWallets = queryPage(rconSocket, currentOffset, 100)
                    .stream()
                    .map(this::applyWalletValues)
                    .collect(Collectors.toList());
            result.addAll(playerWallets);
        }
        return result;
    }

    private PlayerWallet applyWalletValues(PlayerWallet playerWallet) {
        playerWallet.setBronze(parseHex(playerWallet.getBronzeHex()));
        playerWallet.setSilver(parseHex(playerWallet.getSilverHex()));
        playerWallet.setGold(parseHex(playerWallet.getGoldHex()));
        return playerWallet;
    }

    private Integer parseHex(String hex) {
        return StatisticsUtils.parseLittleEndianHex(hex);
    }

    private Collection<PlayerWallet> queryPage(RconSocket rconSocket, Integer offset, Integer limit) {
        String query = loadRconSqlQuery("sql/query_pippi_wallets_paged.sql");
        String finalQuery = query.replace(":offset", offset.toString())
                .replace(":limit", limit.toString());
        log.debug("Player Wallet - page query: {}", finalQuery);
        String response = rconSocket.executeInConnection(finalQuery);
        log.debug("Player Wallet - page query response: {}", response);
        List<PlayerWallet> result = new RconSqlParser<>(PlayerWallet.class)
                .parseMany(response);
        log.debug("Player Wallet - page query result: {}", result);
        return result;
    }

    private int queryCount(RconSocket rconSocket) {
        String query = loadRconSqlQuery("sql/count_pippi_wallets.sql");
        log.debug("Player Wallet - count query: {}", query);
        String response = rconSocket.executeInConnection(query);
        log.debug("Player Wallet - count query response: {}", response);
        RconSqlCountResult countResult = new RconSqlParser<>(RconSqlCountResult.class)
                .parseOne(response);
        log.debug("Player Wallet - count query result: {}", countResult);
        return Integer.parseInt(countResult.getCount());
    }

    @SneakyThrows
    private String loadRconSqlQuery(String name) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(name);
        String rawQuery = StreamUtils.copyToString(resourceAsStream, StandardCharsets.UTF_8);
        // Make query a oneliner
        String sql = Arrays.stream(rawQuery.split("\n"))
                .map(String::trim)
                .collect(Collectors.joining(" "));
        return "sql \"" + sql + "\"";
    }
}
