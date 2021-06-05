package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.entity.OnlinePlayer;
import com.thrallwars.statistics.util.rcon.RconConnectionPool;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlinePlayersRconRepo {
    private final RconConnectionPool rconConnectionPool;

    public OnlinePlayersRconRepo(RconConnectionPool rconConnectionPool) {
        this.rconConnectionPool = rconConnectionPool;
    }

    public List<OnlinePlayer> getOnlinePlayer(RconTarget target) {
        String msg = rconConnectionPool.executePooled(target, "listplayers");
        return new RconSqlParser<>(OnlinePlayer.class).parseMany(msg);
    }
}
