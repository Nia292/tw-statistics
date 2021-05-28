package com.thrallwars.statistics.util.rconsql;

import com.thrallwars.statistics.util.StatisticsUtils;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parses an rcon sql result into a java object.
 */
@Log4j2
public class RconSqlParser<Entity> {

    private final Map<Integer, Field> fieldIndexMap = new HashMap<>();

    private final Class<Entity> clazz;

    public RconSqlParser(Class<Entity> clazz) {
        this.clazz = clazz;
    }

    public Entity parseOne(String sqlString) {
        List<Entity> entities = parseMany(sqlString);
        if (entities.size() <= 0) {
            throw new RuntimeException("No result found for query");
        }
        if (entities.size() >= 2) {
            throw new RuntimeException("More than one result found for query");
        }
        return entities.iterator().next();
    }

    public List<Entity> parseMany(String sqlString) {
        // Check clazz for rcon sql response fields.
        ReflectionUtils.doWithFields(clazz, field -> {
            RconSqlColumn rconColumn = field.getAnnotation(RconSqlColumn.class);
            if (rconColumn != null) {
                fieldIndexMap.put(rconColumn.columnIndex(), field);
            }
        });
        String[] rows = sqlString.split("\n");
        String header = rows[0];
        String errorCodeString = header.substring(0, 4);
        int errorCode = ByteBuffer.wrap(errorCodeString.getBytes(StandardCharsets.UTF_8))
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt();
        log.debug("Parsed error code of query: {}", errorCode);
        // First row is header. We don't care about the header!
        String[] dataRows = Arrays.copyOfRange(rows, 1, rows.length);
        return Arrays.stream(dataRows)
                .map(this::entityFromRow)
                .collect(Collectors.toList());

    }

    @SneakyThrows
    private Entity entityFromRow(String row) {
        // Cells are seperated by pipe (|) in a row and contain whitespace padding
        String[] cells = row.split("\\|");
        Entity entity;
        try {
            entity = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException noSuchMethodException) {
            throw new RuntimeException("No empty constructor found for class " + clazz.getName());
        }
        fieldIndexMap.forEach((colIndex, field) -> {
            String value = cells[colIndex].trim();
            if (colIndex == 0) {
                // Column 0 has #<number>...skip until we find whitespace
                value = skipUntilFirstWhitespace(value).trim();
            }
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, entity, value);
        });
        return entity;
    }

    private String skipUntilFirstWhitespace(String input) {
        char[] chars = input.toCharArray();
        int firstRelevantIndex = 0;
        for (int i = 0; i < chars.length; i++) {
            if (Character.isWhitespace(chars[i])) {
                firstRelevantIndex = i;
                break;
            }
        }
        return input.substring(firstRelevantIndex);
    }
}
