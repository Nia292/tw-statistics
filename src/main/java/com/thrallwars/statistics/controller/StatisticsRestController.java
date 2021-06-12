package com.thrallwars.statistics.controller;

import com.thrallwars.statistics.dto.DataDump;
import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.Player;
import com.thrallwars.statistics.service.RconService;
import com.thrallwars.statistics.service.StatisticsService;
import com.thrallwars.statistics.util.rcon.RconConnectionPool;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("statistics")
@CrossOrigin(origins = "*")
public class StatisticsRestController {

    private final RconService rconService;
    private final StatisticsService statisticsService;
    private final RconConnectionPool rconConnectionPool;

    public StatisticsRestController(RconService rconService, StatisticsService statisticsService, RconConnectionPool rconConnectionPool) {
        this.rconService = rconService;
        this.statisticsService = statisticsService;
        this.rconConnectionPool = rconConnectionPool;
    }

    @GetMapping("player-wallet")
    List<PlayerWallet> getPlayerWallets(@RequestParam(name = "target") String target) {
        return rconService.getPlayerWallets(target);
    }

    @GetMapping("player-banker-wallet")
    List<PlayerBankerWallet> getBankerPlayerWallets(@RequestParam(name = "target") String target) {
        return rconService.getBankerPlayerWallets(target);
    }

    @GetMapping("clan-banker-wallet")
    List<ClanBankerWallet> getBankerClanWallets(@RequestParam(name = "target") String target) {
        return rconService.getBankerClanWallets(target);
    }

    @GetMapping("players")
    List<Player> getAllPlayers(@RequestParam(name = "target") String target) {
        return rconService.getAllPlayers(target);
    }

    @GetMapping("data-dump")
    DataDump getDataDump() {
        return statisticsService.getDataDump();
    }

    @PostMapping("data-dump/discord")
    public void uploadDataDump() {
        statisticsService.uploadZippedDumpToDiscord();
    }

    @GetMapping("data-dump/player-wallet")
    String getStoredPlayerWalletData() {
        String header = "charname,clanid,bronze,silver,gold,server,timestamputc\n";
        return header + getDataDump().getPlayerWallets()
                .stream()
                .map(playerWallet -> String.join(",", List.of(
                        playerWallet.getCharName(),
                        playerWallet.getClanId(),
                        playerWallet.getBronze().toString(),
                        playerWallet.getSilver().toString(),
                        playerWallet.getGold().toString(),
                        playerWallet.getServer(),
                        ((Long) playerWallet.getTimestampUTC().getEpochSecond()).toString()
                ))).collect(Collectors.joining("\n"));
    }

    @PostMapping("data-dump")
    void createDataDump() {
        statisticsService.createDataDump();
    }

    @PostMapping("player-wallet")
    void gatherPlayerWalletData() {
        statisticsService.gatherPlayerWalletData(Instant.now());
    }

    @PostMapping("player-banker-wallet")
    void gatherPlayerBankerWalletData() {
        statisticsService.gatherPlayerBankerData(Instant.now());
    }

    @PostMapping("clan-banker-wallet")
    void gatherClanBankerWalletData() {
        statisticsService.gatherClanBankerData(Instant.now());
    }

    @PostMapping("players")
    void gatherPlayers() {
        statisticsService.gatherPlayerData(Instant.now());
    }

    @PostMapping("online-players")
    void gatherOnlinePlayers() {
        statisticsService.gatherOnlinePlayers(Instant.now());
    }

    @PostMapping("rcon")
    String executeRcon(@RequestBody String body) {
        return rconService.executeSql(body, "TWSW");
    }

    @PostMapping("data-dump/restore")
    void importDumpfile(@RequestBody DataDump dataDump) {
        statisticsService.importDump(dataDump);
    }

}
