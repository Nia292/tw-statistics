package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.dto.PlayerWalletDTO;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.entity.RconSqlCountResult;
import com.thrallwars.statistics.util.StatisticsUtils;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rcon.RconSocket;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.thrallwars.statistics.util.rconsql.RconSqlUtil.loadRconSqlQuery;

@Service
@Log4j2
public class PlayerWalletRconRepo {

    private final RconFactory rconFactory;

    /**
     * Limitation of RCON. We need to limit result sets
     */
    private static final Integer MAX_ROWS_PER_RESULT = 100;

    public PlayerWalletRconRepo(RconFactory rconFactory) {
        this.rconFactory = rconFactory;
    }

    public List<PlayerWallet> queryWallets(RconTarget rconTarget) {
        RconSocket rconSocket = rconFactory.getSocket(rconTarget);
        int size = queryCount(rconSocket);
        Instant timestamp = Instant.now();
        List<PlayerWallet> result = new ArrayList<>();
        // Gather data in pages first
        for (int currentOffset = 0; currentOffset <= size; currentOffset += MAX_ROWS_PER_RESULT) {
            Collection<PlayerWallet> playerWallets = queryPage(rconSocket, currentOffset, MAX_ROWS_PER_RESULT)
                    .stream()
                    .map(this::toPlayerWallet)
                    .collect(Collectors.toList());
            result.addAll(playerWallets);
        }
        // Apply current timestamp and target
        result.forEach(playerWallet -> {
            playerWallet.setTimestamp(timestamp);
            playerWallet.setServer(rconSocket.getServerName());
        });
        return result;
    }

    private PlayerWallet toPlayerWallet(PlayerWalletDTO dto) {
        PlayerWallet playerWallet = new PlayerWallet();
        playerWallet.setBronze(parseHex(dto.getBronzeHex()));
        playerWallet.setSilver(parseHex(dto.getSilverHex()));
        playerWallet.setGold(parseHex(dto.getGoldHex()));
        playerWallet.setCharName(dto.getCharName());
        playerWallet.setClanId(dto.getClanId());
        playerWallet.setRawBronzeValue(dto.getBronzeHex());
        playerWallet.setRawSilverValue(dto.getSilverHex());
        playerWallet.setRawGoldValue(dto.getGoldHex());
        return playerWallet;
    }

    private Integer parseHex(String hex) {
        return StatisticsUtils.parseLittleEndianHex(hex);
    }

    private Collection<PlayerWalletDTO> queryPage(RconSocket rconSocket, Integer offset, Integer limit) {
        String query = loadRconSqlQuery("sql/query_pippi_wallets_paged.sql");
        String finalQuery = query.replace(":offset", offset.toString())
                .replace(":limit", limit.toString());
        log.debug("Player Wallet - page query: {}", finalQuery);
        String response = rconSocket.executeInConnection(finalQuery);
        log.debug("Player Wallet - page query response: {}", response);
        List<PlayerWalletDTO> result = new RconSqlParser<>(PlayerWalletDTO.class)
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

}
