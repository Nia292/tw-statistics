package com.thrallwars.statistics.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsUtilsTest {

    @ParameterizedTest
    @CsvSource(value = {"01000000:1", "18000000:24", "32000000:50", "A0000000:160"}, delimiter = ':')
    void shouldParseLittleEndiansToRightIntegers(String hex, Integer expected) {
        Integer parsed = StatisticsUtils.parseLittleEndianHex(hex);
        assertEquals(parsed, expected);
    }
}
