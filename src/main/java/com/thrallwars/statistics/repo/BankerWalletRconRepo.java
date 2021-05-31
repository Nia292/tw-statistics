package com.thrallwars.statistics.repo;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.dto.ClanBankerWalletDTO;
import com.thrallwars.statistics.dto.PlayerBankerWalletDTO;
import com.thrallwars.statistics.entity.ClanBankerWallet;
import com.thrallwars.statistics.entity.PlayerBankerWallet;
import com.thrallwars.statistics.util.StatisticsUtils;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.thrallwars.statistics.util.rconsql.RconSqlUtil.loadRconSqlQuery;

@Service
@Log4j2
public class BankerWalletRconRepo {

    private final RconFactory rconFactory;

    public BankerWalletRconRepo(RconFactory rconFactory) {
        this.rconFactory = rconFactory;
    }

    public List<PlayerBankerWallet> getPlayerBankerWallets(RconTarget rconTarget) {
        Instant timestamp = Instant.now();
        String query = loadRconSqlQuery("sql/query_pippi_player_banker_wallets.sql");
        log.debug("Banker Player Wallet - query: {}", query);
        String response = rconFactory.getSocket(rconTarget)
                .executeInConnection(query);
        log.debug("Banker Player Wallet - response: {}", response);
        return new RconSqlParser<>(PlayerBankerWalletDTO.class)
                .parseMany(response)
                .stream()
                .map(this::toBankerPlayerWallet)
                .peek(applyMetadtaToPlayerBanker(timestamp, rconTarget.getName()))
                .collect(Collectors.toList());
    }

    public List<ClanBankerWallet> getClanBankerWallets(RconTarget rconTarget) {
        Instant timestamp = Instant.now();
        String query = loadRconSqlQuery("sql/query_pippi_clan_banker_wallets.sql");
        log.debug("Banker Clan Wallet - query: {}", query);
        String response = rconFactory.getSocket(rconTarget)
                .executeInConnection(query);
        log.debug("Banker Clan Wallet - response: {}", response);
        return new RconSqlParser<>(ClanBankerWalletDTO.class)
                .parseMany(response)
                .stream()
                .map(this::toBankerClanWallet)
                .peek(applyMetadataToClanBanker(timestamp, rconTarget.getName()))
                .collect(Collectors.toList());
    }

    private Consumer<PlayerBankerWallet> applyMetadtaToPlayerBanker(Instant timestampUTC, String server) {
        return playerBankerWallet -> {
            playerBankerWallet.setTimestampUTC(timestampUTC);
            playerBankerWallet.setServer(server);
        };
    }

    private Consumer<ClanBankerWallet> applyMetadataToClanBanker(Instant timestampUTC, String server) {
        return clanBankerWallet -> {
            clanBankerWallet.setTimestampUTC(timestampUTC);
            clanBankerWallet.setServer(server);
        };
    }

    private PlayerBankerWallet toBankerPlayerWallet(PlayerBankerWalletDTO playerBankerWalletDTO) {
        return PlayerBankerWallet
                .builder()
                .bronze(StatisticsUtils.parseLittleEndianHex(playerBankerWalletDTO.getBronzeHey()))
                .bronze(StatisticsUtils.parseLittleEndianHex(playerBankerWalletDTO.getSilverHex()))
                .bronze(StatisticsUtils.parseLittleEndianHex(playerBankerWalletDTO.getGoldHex()))
                .charName(playerBankerWalletDTO.getCharName())
                .ownerId(playerBankerWalletDTO.getOwnerId())
                .build();
    }

    private ClanBankerWallet toBankerClanWallet(ClanBankerWalletDTO bankerClanWalletDTO) {
        return ClanBankerWallet
                .builder()
                .bronze(StatisticsUtils.parseLittleEndianHex(bankerClanWalletDTO.getBronzeHey()))
                .silver(StatisticsUtils.parseLittleEndianHex(bankerClanWalletDTO.getSilverHex()))
                .gold(StatisticsUtils.parseLittleEndianHex(bankerClanWalletDTO.getGoldHex()))
                .clanId(bankerClanWalletDTO.getGuildId())
                .ownerId(bankerClanWalletDTO.getOwnerId())
                .build();
    }
}
