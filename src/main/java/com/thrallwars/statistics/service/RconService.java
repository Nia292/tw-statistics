package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.OnlinePlayer;
import com.thrallwars.statistics.entity.OnlinePlayers;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.PlayerWalletRconRepo;
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
    private final PlayerWalletRconRepo playerWalletRconRepo;

    public RconService(RconFactory rconFactory, ServiceConfig serviceConfig, PlayerWalletRconRepo playerWalletRconRepo) {
        this.rconFactory = rconFactory;
        this.serviceConfig = serviceConfig;
        this.playerWalletRconRepo = playerWalletRconRepo;
    }

    public OnlinePlayers getOnlinePlayers(String target) {
        String playerList = getOnlinePlayersPlain(target);
        RconSqlParser<OnlinePlayer> parser = new RconSqlParser<>(OnlinePlayer.class);
        List<OnlinePlayer> players = parser.parseMany(playerList);
        return OnlinePlayers.builder()
                .time(OffsetDateTime.now())
                .players(players).build();
    }

    public List<PlayerWallet> getPlayerWallets(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        RconSocket socket = rconFactory.getSocket(rconTarget);
        return playerWalletRconRepo.queryWallets(socket);
    }

    public String getOnlinePlayersPlain(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        RconSocket socket = rconFactory.getSocket(rconTarget);
        return socket.executeInConnection("listplayers");
    }
}
