package com.thrallwars.statistics.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public class StatisticsUtils {

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

}
