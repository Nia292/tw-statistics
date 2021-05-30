package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.*;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rcon.RconSocket;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class StatisticsService {

    private final PlayerWalletRconRepo playerWalletRconRepo;
    private final BankerWalletRconRepo bankerWalletRconRepo;
    private final PlayerWalletRepo playerWalletRepo;
    private final PlayerBankerWalletRepo playerBankerWalletRepo;
    private final ClanBankerWalletRepo clanBankerWalletRepo;
    private final ServiceConfig serviceConfig;

    public StatisticsService(PlayerWalletRconRepo playerWalletRconRepo,
                             BankerWalletRconRepo bankerWalletRconRepo,
                             PlayerWalletRepo playerWalletRepo,
                             PlayerBankerWalletRepo playerBankerWalletRepo,
                             ClanBankerWalletRepo clanBankerWalletRepo,
                             ServiceConfig serviceConfig
    ) {
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.bankerWalletRconRepo = bankerWalletRconRepo;
        this.playerWalletRepo = playerWalletRepo;
        this.playerBankerWalletRepo = playerBankerWalletRepo;
        this.clanBankerWalletRepo = clanBankerWalletRepo;
        this.serviceConfig = serviceConfig;
    }

    public void gatherPlayerBankerData() {
        runForEachActiveTarget(this::gatherPlayerBankerData);
    }

    public void gatherClanBankerData() {
        runForEachActiveTarget(this::gatherClanBankerData);
    }

    public void gatherPlayerWalletData() {
        runForEachActiveTarget(this::gatherWalletDataForTarget);
    }

    private void runForEachActiveTarget(Consumer<RconTarget> consumer) {
        serviceConfig.getTargets().stream()
                .filter(RconTarget::isGather)
                .forEach(consumer);
    }

    private void gatherPlayerBankerData(RconTarget rconTarget) {
        List<PlayerBankerWallet> playerWallets = bankerWalletRconRepo.getPlayerBankerWallets(rconTarget);
        playerWallets.forEach(playerBankerWalletRepo::save);
    }

    private void gatherClanBankerData(RconTarget rconTarget) {
        List<ClanBankerWallet> playerWallets = bankerWalletRconRepo.getClanBankerWallets(rconTarget);
        playerWallets.forEach(clanBankerWalletRepo::save);
    }

    private void gatherWalletDataForTarget(RconTarget rconTarget) {
        List<PlayerWallet> playerWallets = playerWalletRconRepo.queryWallets(rconTarget);
        playerWallets.forEach(playerWalletRepo::save);
    }
}
