package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.dto.PlayerDTO;
import com.thrallwars.statistics.entity.RconSqlCountResult;
import com.thrallwars.statistics.util.rcon.RconConnectionPool;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import com.thrallwars.statistics.util.rconsql.RconSqlUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PlayerRconRepo {

    private final RconConnectionPool rconConnectionPool;

    /**
     * Limitation of RCON. We need to limit result sets
     */
    private static final Integer MAX_ROWS_PER_RESULT = 100;


    public PlayerRconRepo(RconConnectionPool rconConnectionPool) {
        this.rconConnectionPool = rconConnectionPool;
    }

    public List<Player> getAllPlayers(RconTarget rconTarget) {
        Instant timestamp = Instant.now();
        int size = countQuery(rconTarget);
        List<PlayerDTO> result = new ArrayList<>();
        // Gather data in pages first
        for (int currentOffset = 0; currentOffset <= size; currentOffset += MAX_ROWS_PER_RESULT) {
            Collection<PlayerDTO> players = queryPage(rconTarget, currentOffset, MAX_ROWS_PER_RESULT);
            result.addAll(players);
        }
        return result.stream()
                .map(toPlayer(rconTarget, timestamp))
                .collect(Collectors.toList());
    }

    private Function<PlayerDTO, Player> toPlayer(RconTarget rconTarget, Instant timestamp) {
        return playerDTO -> Player.builder()
                .charName(playerDTO.getCharName())
                .clanId(playerDTO.getClanId())
                .clanName(playerDTO.getClanName())
                .lastTimeOnlineTS(playerDTO.getLastTimeOnlineTS())
                .level(playerDTO.getLevel())
                .playerId(playerDTO.getPlayerId())
                .timestampUTC(timestamp)
                .server(rconTarget.getName())
                .build();
    }

    private List<PlayerDTO> queryPage(RconTarget rconTarget, Integer offset, Integer limit) {
        String query = RconSqlUtil.loadRconSqlQuery("sql/query_get_players.sql");
        query = query.replace(":offset", offset.toString())
                .replace(":limit", limit.toString());
        log.debug("All players - query: {}", query);
        String response = rconConnectionPool.executePooled(rconTarget, query);
        log.debug("All players - response: {}", response);
        return new RconSqlParser<>(PlayerDTO.class)
                .parseMany(response);
    }

    private int countQuery(RconTarget rconTarget) {
        String query = RconSqlUtil.loadRconSqlQuery("sql/count_get_players.sql");
        log.debug("Count players - query: {}", query);
        String response = rconConnectionPool.executePooled(rconTarget, query);
        log.debug("Count players - response: {}", response);
        String countString = new RconSqlParser<>(RconSqlCountResult.class)
                .parseOne(response)
                .getCount();
        log.debug("Count players - result: {}", countString);
        return Integer.parseInt(countString);
    }
}
