package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.dto.DataDump;
import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.*;
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
    private final PlayerRconRepo playerRconRepo;
    private final PlayerRepo playerRepo;

    public StatisticsService(PlayerWalletRconRepo playerWalletRconRepo,
                             BankerWalletRconRepo bankerWalletRconRepo,
                             PlayerWalletRepo playerWalletRepo,
                             PlayerBankerWalletRepo playerBankerWalletRepo,
                             ClanBankerWalletRepo clanBankerWalletRepo,
                             ServiceConfig serviceConfig,
                             PlayerRconRepo playerRconRepo, PlayerRepo playerRepo) {
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.bankerWalletRconRepo = bankerWalletRconRepo;
        this.playerWalletRepo = playerWalletRepo;
        this.playerBankerWalletRepo = playerBankerWalletRepo;
        this.clanBankerWalletRepo = clanBankerWalletRepo;
        this.serviceConfig = serviceConfig;
        this.playerRconRepo = playerRconRepo;
        this.playerRepo = playerRepo;
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

    public void gatherPlayerData() {
        runForEachActiveTarget(this::gatherPlayerDataForTarget);
    }

    public DataDump getDataDump() {
        DataDump dataDump = new DataDump();
        dataDump.setClanBankerWallets(clanBankerWalletRepo.findAll());
        dataDump.setPlayerBankerWallets(playerBankerWalletRepo.findAll());
        dataDump.setPlayerWallets(playerWalletRepo.findAll());
        return dataDump;
    }

    private void runForEachActiveTarget(Consumer<RconTarget> consumer) {
        serviceConfig.getTargets().stream()
                .filter(RconTarget::isGather)
                .forEach(consumer);
    }

    private void gatherPlayerBankerData(RconTarget rconTarget) {
        List<PlayerBankerWallet> playerWallets = bankerWalletRconRepo.getPlayerBankerWallets(rconTarget);
        playerBankerWalletRepo.saveAll(playerWallets);
    }

    private void gatherClanBankerData(RconTarget rconTarget) {
        List<ClanBankerWallet> playerWallets = bankerWalletRconRepo.getClanBankerWallets(rconTarget);
        clanBankerWalletRepo.saveAll(playerWallets);
    }

    private void gatherWalletDataForTarget(RconTarget rconTarget) {
        List<PlayerWallet> playerWallets = playerWalletRconRepo.queryWallets(rconTarget);
        playerWalletRepo.saveAll(playerWallets);
    }

    private void gatherPlayerDataForTarget(RconTarget rconTarget) {
        List<Player> players = playerRconRepo.getAllPlayers(rconTarget);
        playerRepo.saveAll(players);
    }
}
