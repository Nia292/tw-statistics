package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.dto.DataDump;
import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.GatheringError;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Log4j2
public class WalletStatisticsService {

    private final PlayerWalletRconRepo playerWalletRconRepo;
    private final BankerWalletRconRepo bankerWalletRconRepo;
    private final PlayerWalletRepo playerWalletRepo;
    private final PlayerBankerWalletRepo playerBankerWalletRepo;
    private final ClanBankerWalletRepo clanBankerWalletRepo;
    private final ServiceConfig serviceConfig;
    private final PlayerRconRepo playerRconRepo;
    private final PlayerRepo playerRepo;
    private final GatheringErrorRepo gatheringErrorRepo;

    public WalletStatisticsService(PlayerWalletRconRepo playerWalletRconRepo,
                                   BankerWalletRconRepo bankerWalletRconRepo,
                                   PlayerWalletRepo playerWalletRepo,
                                   PlayerBankerWalletRepo playerBankerWalletRepo,
                                   ClanBankerWalletRepo clanBankerWalletRepo,
                                   ServiceConfig serviceConfig,
                                   PlayerRconRepo playerRconRepo,
                                   GatheringErrorRepo gatheringErrorRepo,
                                   PlayerRepo playerRepo) {
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.bankerWalletRconRepo = bankerWalletRconRepo;
        this.playerWalletRepo = playerWalletRepo;
        this.playerBankerWalletRepo = playerBankerWalletRepo;
        this.clanBankerWalletRepo = clanBankerWalletRepo;
        this.serviceConfig = serviceConfig;
        this.playerRconRepo = playerRconRepo;
        this.playerRepo = playerRepo;
        this.gatheringErrorRepo = gatheringErrorRepo;
    }

    public void gatherPlayerBankerData(Instant timestamp) {
        runForEachActiveTarget(rconTarget -> gatherPlayerBankerData(rconTarget, timestamp));
    }

    public void gatherClanBankerData(Instant timestamp) {
        runForEachActiveTarget(rconTarget -> gatherClanBankerData(rconTarget, timestamp));
    }

    public void gatherPlayerWalletData(Instant timestamp) {
        runForEachActiveTarget(rconTarget -> gatherWalletDataForTarget(rconTarget, timestamp));
    }

    public void gatherPlayerData(Instant timestamp) {
        runForEachActiveTarget(rconTarget -> gatherPlayerDataForTarget(rconTarget, timestamp));
    }

    public void createDataDump() {
        Instant instant = Instant.now();
        runLoggingException(() ->  gatherPlayerBankerData(instant), "Gather Player Banker Data");
        runLoggingException(() ->  gatherClanBankerData(instant), "Gather Clan Banker Data");
        runLoggingException(() ->  gatherPlayerWalletData(instant), "Gather Player Wallet Data");
        runLoggingException(() ->  gatherPlayerData(instant), "Gather Player Data");
    }

    public DataDump getDataDump() {
        DataDump dataDump = new DataDump();
        dataDump.setClanBankerWallets(clanBankerWalletRepo.findAll());
        dataDump.setPlayerBankerWallets(playerBankerWalletRepo.findAll());
        dataDump.setPlayerWallets(playerWalletRepo.findAll());
        dataDump.setPlayers(playerRepo.findAll());
        return dataDump;
    }

    private void runForEachActiveTarget(Consumer<RconTarget> consumer) {
        serviceConfig.getTargets().stream()
                .filter(RconTarget::isGather)
                .forEach(consumer);
    }

    private void gatherPlayerBankerData(RconTarget rconTarget, Instant timestamp) {
        List<PlayerBankerWallet> playerWallets = bankerWalletRconRepo.getPlayerBankerWallets(rconTarget);
        playerWallets.forEach(walle -> walle.setTimestampUTC(timestamp));
        playerBankerWalletRepo.saveAll(playerWallets);
    }

    private void gatherClanBankerData(RconTarget rconTarget, Instant timestamp) {
        List<ClanBankerWallet> wallets = bankerWalletRconRepo.getClanBankerWallets(rconTarget);
        wallets.forEach(wallet -> wallet.setTimestampUTC(timestamp));
        clanBankerWalletRepo.saveAll(wallets);
    }

    private void gatherWalletDataForTarget(RconTarget rconTarget, Instant timestamp) {
        List<PlayerWallet> wallets = playerWalletRconRepo.queryWallets(rconTarget);
        wallets.forEach(wallet -> wallet.setTimestampUTC(timestamp));
        playerWalletRepo.saveAll(wallets);
    }

    private void gatherPlayerDataForTarget(RconTarget rconTarget,Instant timestamp) {
        List<Player> players = playerRconRepo.getAllPlayers(rconTarget);
        players.forEach(player -> player.setTimestampUTC(timestamp));
        playerRepo.saveAll(players);
    }

    private void runLoggingException(Runnable runnable, String operation) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("Failed {}", operation, e);
            GatheringError error = GatheringError.builder()
                    .operation(operation)
                    .timestampUTC(Instant.now())
                    .build();
            gatheringErrorRepo.save(error);
        }
    }
}
