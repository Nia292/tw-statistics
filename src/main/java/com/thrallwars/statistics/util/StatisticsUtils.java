package com.thrallwars.statistics.util;

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
}
