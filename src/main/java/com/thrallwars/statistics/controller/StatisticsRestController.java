package com.thrallwars.statistics.controller;

import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.OnlinePlayers;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.service.RconService;
import com.thrallwars.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("statistics")
public class StatisticsRestController {

    private final RconService rconService;
    private final StatisticsService statisticsService;

    public StatisticsRestController(RconService rconService, StatisticsService statisticsService) {
        this.rconService = rconService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("online-players")
    OnlinePlayers getOnlinePlayers(@RequestParam(name = "target") String target) {
        return rconService.getOnlinePlayers(target);
    }

    @GetMapping("plain/online-players")
    String getOnlinePlayersPlain(@RequestParam(name = "target") String target) {
        return rconService.getOnlinePlayersPlain(target);
    }

    @GetMapping("player-wallet")
    List<PlayerWallet> getPlayerWallets(@RequestParam(name = "target") String target) {
        return rconService.getPlayerWallets(target);
    }

    @PostMapping("player-wallet")
    void gatherPlayerWalletData() {
        statisticsService.gatherPlayerWalletData();
    }

    @PostMapping("player-banker-wallet")
    void gatherPlayerBankerWalletData() {
        statisticsService.gatherPlayerBankerData();
    }

    @PostMapping("clan-banker-wallet")
    void gatherClanBankerWalletData() {
        statisticsService.gatherClanBankerData();
    }

    @GetMapping("player-banker-wallet")
    List<PlayerBankerWallet> getBankerPlayerWallets(@RequestParam(name = "target") String target) {
        return rconService.getBankerPlayerWallets(target);
    }

    @GetMapping("clan-banker-wallet")
    List<ClanBankerWallet> getBankerClanWallets(@RequestParam(name = "target") String target) {
        return rconService.getBankerClanWallets(target);
    }
}
