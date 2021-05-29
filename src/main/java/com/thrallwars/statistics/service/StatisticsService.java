package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.PlayerWalletRconRepo;
import com.thrallwars.statistics.repo.PlayerWalletRepo;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rcon.RconSocket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final PlayerWalletRconRepo playerWalletRconRepo;
    private final PlayerWalletRepo playerWalletRepo;
    private final ServiceConfig serviceConfig;
    private final RconFactory rconFactory;

    public StatisticsService(PlayerWalletRconRepo playerWalletRconRepo, PlayerWalletRepo playerWalletRepo, ServiceConfig serviceConfig, RconFactory rconFactory) {
        this.playerWalletRconRepo = playerWalletRconRepo;
        this.playerWalletRepo = playerWalletRepo;
        this.serviceConfig = serviceConfig;
        this.rconFactory = rconFactory;
    }

    public void gatherPlayerWalletData() {
        serviceConfig.getTargets().stream()
                .filter(RconTarget::isGather)
                .forEach(this::gatherWalletDataForTarget);

    }

    private void gatherWalletDataForTarget(RconTarget rconTarget) {
        RconSocket socket = rconFactory.getSocket(rconTarget);
        List<PlayerWallet> playerWallets = playerWalletRconRepo.queryWallets(socket);
        playerWallets.forEach(playerWalletRepo::save);
    }
}
