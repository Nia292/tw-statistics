package com.thrallwars.statistics.controller;

import com.thrallwars.statistics.entity.OnlinePlayers;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.service.RconService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("statistics")
public class StatisticsRestController {

    private final RconService rconService;

    public StatisticsRestController(RconService rconService) {
        this.rconService = rconService;
    }

    @GetMapping("online-players")
    OnlinePlayers getOnlinePlayers(@RequestParam(name = "target", required = true) String target) {
        return rconService.getOnlinePlayers(target);
    }

    @GetMapping("plain/online-players")
    String getOnlinePlayersPlain(@RequestParam(name = "target", required = true) String target) {
        return rconService.getOnlinePlayersPlain(target);
    }

    @GetMapping("player-wallet")
    List<PlayerWallet> getPlayerWallets(@RequestParam(name = "target", required = true) String target) {
        return rconService.getPlayerWallets(target);
    }

    @GetMapping("plain/player-wallet")
    String getPlayerWalletsPlain(@RequestParam(name = "target", required = true) String target) {
//        return rconService.getPippiGoldPlain(target);
        return "";
    }
}
