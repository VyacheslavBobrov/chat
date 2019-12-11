package ru.bobrov.vyacheslav.chat.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DateUtils {
    public static Timestamp parse(String dateStr) {
        return Timestamp.valueOf(LocalDateTime.parse(dateStr));
    }
}
