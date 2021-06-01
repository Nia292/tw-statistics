package com.thrallwars.statistics.dto;

import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.OnlinePlayer;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.repo.Player;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataDump {
    private List<PlayerWallet> playerWallets = new ArrayList<>();
    private List<PlayerBankerWallet> playerBankerWallets = new ArrayList<>();
    private List<ClanBankerWallet> clanBankerWallets = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
}
