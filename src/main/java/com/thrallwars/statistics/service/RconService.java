package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.*;
import com.thrallwars.statistics.repo.BankerWalletRconRepo;
import com.thrallwars.statistics.repo.Player;
import com.thrallwars.statistics.repo.PlayerRconRepo;
import com.thrallwars.statistics.repo.PlayerWalletRconRepo;
import com.thrallwars.statistics.util.rcon.RconConnectionPool;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RconService {

    private final RconConnectionPool rconConnectionPool;
    private final ServiceConfig serviceConfig;
    private final PlayerWalletRconRepo playerWalletRconRepo;
    private final BankerWalletRconRepo bankerWalletRconRepo;
    private final PlayerRconRepo playerRconRepo;

    public RconService(RconConnectionPool rconConnectionPool, ServiceConfig serviceConfig, PlayerWalletRconRepo playerWalletRconRepo, BankerWalletRconRepo bankerWalletRconRepo, PlayerRconRepo playerRconRepo) {
        this.rconConnectionPool = rconConnectionPool;
        this.serviceConfig = serviceConfig;
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.bankerWalletRconRepo = bankerWalletRconRepo;
        this.playerRconRepo = playerRconRepo;
    }

    public List<PlayerWallet> getPlayerWallets(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        return playerWalletRconRepo.queryWallets(rconTarget);
    }


    public List<PlayerBankerWallet> getBankerPlayerWallets(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        return bankerWalletRconRepo.getPlayerBankerWallets(rconTarget);
    }

    public List<ClanBankerWallet> getBankerClanWallets(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        return bankerWalletRconRepo.getClanBankerWallets(rconTarget);
    }

    public List<Player> getAllPlayers(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        return playerRconRepo.getAllPlayers(rconTarget);
    }
}
