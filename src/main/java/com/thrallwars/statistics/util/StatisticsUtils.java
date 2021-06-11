package com.thrallwars.statistics.util;

import com.thrallwars.statistics.dto.DataDump;
import com.thrallwars.statistics.repo.Player;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public class StatisticsUtils {

    private static final DigestUtils digestUtils = new DigestUtils(MessageDigestAlgorithms.SHA_512);

    public static Integer parseLittleEndianHex(String hex) {
        String s = new StringBuilder()
                .append(hex.charAt(6))
                .append(hex.charAt(7))
                .append(hex.charAt(4))
                .append(hex.charAt(5))
                .append(hex.charAt(2))
                .append(hex.charAt(3))
                .append(hex.charAt(0))
                .append(hex.charAt(1))
                .toString();
        return Integer.parseInt(s, 16);
    }

    public static byte[] compressStringToGzip(String data) throws IOException {
        byte[] dataToCompress = data.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length);
        try (byteStream) {
            try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                zipStream.write(dataToCompress);
            }
        }
        return byteStream.toByteArray();
    }


    public static void anonymizeDataDump(DataDump dataDump) {

    }

    private static void anonymizePlayer(Player player) {
        player.setPlayerId(digestUtils.digestAsHex(player.getPlayerId()));
        player.setCharName(digestUtils.digestAsHex(player.getCharName()));
        player.setClanId(digestUtils.digestAsHex(player.getClanId()));
        player.setClanName(digestUtils.digestAsHex(player.getClanName()));
    }

}
