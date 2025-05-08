package com.mad.prescriptionmanagementapp.data.database;

import androidx.room.TypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mad.prescriptionmanagementapp.util.ReminderStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Converters {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public enum Frequency {
        DAILY, EVERY_N_DAYS, SPECIFIC_DATES, UNKNOWN
    }

    @TypeConverter
    public static ReminderStatus toReminderStatus(String name) {
        return ReminderStatus.valueOf(name);
    }

    @TypeConverter
    public static String fromReminderStatus(ReminderStatus status) {
        return status.name();
    }

    @TypeConverter
    public static Frequency toFrequency(String value) {
        if (value == null) return Frequency.UNKNOWN;
        try {
            return Frequency.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return Frequency.UNKNOWN;
        }
    }

    @TypeConverter
    public static String fromFrequency(Frequency frequency) {
        return frequency == null ? null : frequency.name();
    }

    @TypeConverter
    public static List<String> fromStringList(String value) {
        if (value == null) return Collections.emptyList();
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @TypeConverter
    public static String fromArrayListOfString(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    @TypeConverter
    public static List<Integer> fromIntegerList(String value) {
        if (value == null) return Collections.emptyList();
        try {
            return objectMapper.readValue(value, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @TypeConverter
    public static String fromArrayListOfInteger(List<Integer> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value, localDateFormatter);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.format(localDateFormatter);
    }
}
