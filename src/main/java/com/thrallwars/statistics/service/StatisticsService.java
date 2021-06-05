package com.thrallwars.statistics.service;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.dto.DataDump;
import com.thrallwars.statistics.entity.*;
import com.thrallwars.statistics.repo.*;
import com.thrallwars.statistics.util.StatisticsUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Log4j2
public class StatisticsService {

    private final PlayerWalletRconRepo playerWalletRconRepo;
    private final BankerWalletRconRepo bankerWalletRconRepo;
    private final PlayerWalletRepo playerWalletRepo;
    private final PlayerBankerWalletRepo playerBankerWalletRepo;
    private final ClanBankerWalletRepo clanBankerWalletRepo;
    private final ServiceConfig serviceConfig;
    private final PlayerRconRepo playerRconRepo;
    private final PlayerRepo playerRepo;
    private final GatheringErrorRepo gatheringErrorRepo;
    private final DiscordWebhookService discordWebhookService;
    private final ObjectMapper objectMapper;
    private final OnlinePlayersRconRepo onlinePlayersRconRepo;
    private final OnlinePlayersRepo onlinePlayersRepo;

    public StatisticsService(PlayerWalletRconRepo playerWalletRconRepo,
                             BankerWalletRconRepo bankerWalletRconRepo,
                             PlayerWalletRepo playerWalletRepo,
                             PlayerBankerWalletRepo playerBankerWalletRepo,
                             ClanBankerWalletRepo clanBankerWalletRepo,
                             ServiceConfig serviceConfig,
                             PlayerRconRepo playerRconRepo,
                             GatheringErrorRepo gatheringErrorRepo,
                             PlayerRepo playerRepo,
                             DiscordWebhookService discordWebhookService,
                             ObjectMapper objectMapper,
                             OnlinePlayersRconRepo onlinePlayersRconRepo,
                             OnlinePlayersRepo onlinePlayersRepo) {
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.bankerWalletRconRepo = bankerWalletRconRepo;
        this.playerWalletRepo = playerWalletRepo;
        this.playerBankerWalletRepo = playerBankerWalletRepo;
        this.clanBankerWalletRepo = clanBankerWalletRepo;
        this.serviceConfig = serviceConfig;
        this.playerRconRepo = playerRconRepo;
        this.playerRepo = playerRepo;
        this.gatheringErrorRepo = gatheringErrorRepo;
        this.discordWebhookService = discordWebhookService;
        this.objectMapper = objectMapper;
        this.onlinePlayersRconRepo = onlinePlayersRconRepo;
        this.onlinePlayersRepo = onlinePlayersRepo;
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

    public void gatherOnlinePlayers(Instant timestamp) {
        runForEachActiveTarget(rconTarget -> gatherOnlinePlayersForTarget(rconTarget, timestamp));
    }

    @SneakyThrows
    public void uploadZippedDumpToDiscord() {
        DataDump dataDump = getDataDump();
        String dataDumpAsString = objectMapper.writer(new DefaultPrettyPrinter()).writeValueAsString(dataDump);
        byte[] bytes = StatisticsUtils.compressStringToGzip(dataDumpAsString);
        DateFormat df = new SimpleDateFormat("dd_MMM_yyyy_kk_mm_ss");
        String format = df.format(new Date());
        discordWebhookService.publishInfo("Current data dump with all data, json format, gzip compressed will upload soon.");
        discordWebhookService.publishFile(bytes, "data_dump_" + format + ".json.gzip");
    }

    public void createDataDump() {
        Instant instant = Instant.now();
        runLoggingException(() ->  gatherPlayerBankerData(instant), "Gather Player Banker Data");
        runLoggingException(() ->  gatherClanBankerData(instant), "Gather Clan Banker Data");
        runLoggingException(() ->  gatherPlayerWalletData(instant), "Gather Player Wallet Data");
        runLoggingException(() ->  gatherPlayerData(instant), "Gather Player Data");
        runLoggingException(() ->  gatherOnlinePlayers(instant), "Gather Online Players Data");
    }

    public DataDump getDataDump() {
        DataDump dataDump = new DataDump();
        dataDump.setClanBankerWallets(clanBankerWalletRepo.findAll());
        dataDump.setPlayerBankerWallets(playerBankerWalletRepo.findAll());
        dataDump.setPlayerWallets(playerWalletRepo.findAll());
        dataDump.setPlayers(playerRepo.findAll());
        dataDump.setOnlinePlayers(onlinePlayersRepo.findAll());
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

    private void gatherOnlinePlayersForTarget(RconTarget rconTarget, Instant timestamp) {
        List<OnlinePlayer> onlinePlayers = onlinePlayersRconRepo.getOnlinePlayer(rconTarget);
        onlinePlayers.forEach(onlinePlayer -> onlinePlayer.setTimestampUTC(timestamp));
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
            String discordMsg = "Failed " + operation + " @ " + Instant.now().toString() + " with error " + e.getMessage() + ". Stacktrace: ";
            discordWebhookService.publishInfo(discordMsg);
            var stacktrace = Arrays.stream(e.getStackTrace())
                    .map(stackTraceElement -> stackTraceElement.toString())
                    .collect(Collectors.joining("\n"));
            discordWebhookService.publishInfo(stacktrace);
        }
    }
}
