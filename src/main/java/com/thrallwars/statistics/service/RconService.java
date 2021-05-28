package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.OnlinePlayer;
import com.thrallwars.statistics.entity.OnlinePlayers;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rcon.RconSocket;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RconService {

    private final RconFactory rconFactory;
    private final ServiceConfig serviceConfig;

    public RconService(RconFactory rconFactory, ServiceConfig serviceConfig) {
        this.rconFactory = rconFactory;
        this.serviceConfig = serviceConfig;
    }

    public OnlinePlayers getOnlinePlayers(String target) {
        String playerList = getOnlinePlayersPlain(target);
        RconSqlParser<OnlinePlayer> parser = new RconSqlParser<>(OnlinePlayer.class);
        List<OnlinePlayer> players = parser.parseMany(playerList);
        return OnlinePlayers.builder()
                .time(OffsetDateTime.now())
                .players(players).build();
    }

    public String getOnlinePlayersPlain(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        RconSocket socket = rconFactory.getSocket(rconTarget.getHost(), rconTarget.getPort(), rconTarget.getPassword());
        return socket.executeInConnection("listplayers");
    }
}
