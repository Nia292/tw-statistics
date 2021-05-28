package com.thrallwars.statistics.util.rconsql;

import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Parses an rcon sql result into a java object.
 */
public class RconSqlParser<Entity> {

    private final Map<Integer, Field> fieldIndexMap = new HashMap<>();

    private final Class<Entity> clazz;

    public RconSqlParser(Class<Entity> clazz) {
        this.clazz = clazz;
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
            ReflectionUtils.makeAccessible(field);
            ReflectionUtils.setField(field, entity, value);
        });
        return entity;
    }
}
