package ru.bobrov.vyacheslav.chat.services;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.EntityWithTimeInfo;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.NotImplementedException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

public class Utils {
    static public void assertNotNull(Object val, String message) {
        if (val == null)
            throw new IllegalArgumentException(message);
    }

    static public void assertNotBlank(String val, String message) {
        if (isBlank(val))
            throw new IllegalArgumentException(message);
    }

    static public boolean isBlank(String val) {
        return val == null || val.isBlank();
    }

    static public void checkTimeInfo(EntityWithTimeInfo entity) {
        assertNotNull(entity.getCreated(), entity.getClass().getSimpleName() + " created is null");
        assertNotNull(entity.getUpdated(), entity.getClass().getSimpleName() + " updated is null");
    }

    static public void initTime(EntityWithTimeInfo entity) {
        updateTime(entity);
        entity.setCreated(entity.getUpdated());
    }

    static public void updateTime(EntityWithTimeInfo entity) {
        entity.setUpdated(new Timestamp(new Date().getTime()));
    }

    static public <T> T toDo() {
        return toDo("Not implemented");
    }

    static public <T> T toDo(String message) {
        throw new NotImplementedException(message);
    }
}
