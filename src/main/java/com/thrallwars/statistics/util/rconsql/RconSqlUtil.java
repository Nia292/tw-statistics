package com.thrallwars.statistics.util.rconsql;

import lombok.SneakyThrows;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class RconSqlUtil {

    @SneakyThrows
    public static String loadRconSqlQuery(String name) {
        InputStream resourceAsStream = RconSqlUtil.class.getClassLoader().getResourceAsStream(name);
        String rawQuery = StreamUtils.copyToString(resourceAsStream, StandardCharsets.UTF_8);
        // Need to remove linesbreaks apparently. No idea why it's necessary
        String sql = Arrays.stream(rawQuery.split("\n"))
                .map(String::trim)
                // Remove comment lines, rcon sql can't handle them.
                .filter(s -> !s.startsWith("--"))
                .collect(Collectors.joining(" "));
        // wrap statement in sql "<statement>"
        return "sql \"" + sql + "\"";
    }
}
