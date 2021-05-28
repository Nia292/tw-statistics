package com.thrallwars.statistics.service;

import com.thrallwars.statistics.config.RconTarget;
import com.thrallwars.statistics.config.ServiceConfig;
import com.thrallwars.statistics.entity.OnlinePlayer;
import com.thrallwars.statistics.entity.OnlinePlayers;
import com.thrallwars.statistics.entity.PlayerWallet;
import com.thrallwars.statistics.util.StatisticsUtils;
import com.thrallwars.statistics.util.rcon.RconFactory;
import com.thrallwars.statistics.util.rcon.RconSocket;
import com.thrallwars.statistics.util.rconsql.RconSqlParser;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RconService {

    private final RconFactory rconFactory;
    private final ServiceConfig serviceConfig;

    public RconService(RconFactory rconFactory, ServiceConfig serviceConfig) {
        this.rconFactory = rconFactory;
        this.serviceConfig = serviceConfig;
    }

    public OnlinePlayers getOnlinePlayers(String target) {
        String playerList = getOnlinePlayersPlain(target);
        RconSqlParser<OnlinePlayer> parser = new RconSqlParser<>(OnlinePlayer.class);
        List<OnlinePlayer> players = parser.parseMany(playerList);
        return OnlinePlayers.builder()
                .time(OffsetDateTime.now())
                .players(players).build();
    }

    public List<PlayerWallet> getPlayerWallets(String target) {
        String walletsText = getPippiGoldPlain(target);
        RconSqlParser<PlayerWallet> parser = new RconSqlParser<>(PlayerWallet.class);
        return parser.parseMany(walletsText)
                .stream()
                .map(this::parseHex)
                .collect(Collectors.toList());
    }

    private PlayerWallet parseHex(PlayerWallet playerWallet) {
        playerWallet.setBronze(parseHex(playerWallet.getBronzeHex()));
        playerWallet.setSilver(parseHex(playerWallet.getSilverHex()));
        playerWallet.setGold(parseHex(playerWallet.getGoldHex()));
        return playerWallet;
    }

    private Integer parseHex(String hex) {
        return StatisticsUtils.parseLittleEndianHex(hex);
    }

    public String getOnlinePlayersPlain(String target) {
        RconTarget rconTarget = serviceConfig.findTarget(target);
        RconSocket socket = rconFactory.getSocket(rconTarget.getHost(), rconTarget.getPort(), rconTarget.getPassword());
        return socket.executeInConnection("listplayers");
    }

    public String getPippiGoldPlain(String target) {
        // What's up with these hex(substr()) operations?
        // a property is, in essence, a serialized game object
        // Through a complex process (try and error...), I found out which bytes to read to get
        // the integer values from the blob
        // To transmit them over rcon, they are converted into a hex string.
        String sql = """ 
                    select char.id,
                           char.char_name,
                           char.guild,
                           hex(SUBSTR(props.value, 0x4A, 4)) as gold,
                           hex(SUBSTR(props.value, 0x95, 4)) as silver,
                           hex(SUBSTR(props.value, 0xE0, 4)) as bronze
                    from characters char
                             join properties props on char.id = props.object_id
                    where props.name = 'Pippi_WalletComponent_C.walletAmount';
                    """;
        // need to make this sql a one-liner
        Stream.of(sql.split("\n"))
                .map(String::trim)
                .collect(Collectors.joining(" "));
        RconTarget rconTarget = serviceConfig.findTarget(target);
        RconSocket socket = rconFactory.getSocket(rconTarget.getHost(), rconTarget.getPort(), rconTarget.getPassword());
        return socket.executeInConnection("sql \"" + sql + "\"");
    }
}
