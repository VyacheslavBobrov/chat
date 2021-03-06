package ru.bobrov.vyacheslav.chat.services.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.EntityWithTimeInfo;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.NotImplementedException;

import java.sql.Timestamp;
import java.util.Date;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Utils {
    static Translator translator;

    @Autowired
    public Utils(Translator translator) {
        Utils.translator = translator;
    }

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

    @SuppressWarnings("unused")
    static public <T> T toDo() {
        return toDo(
                translator.translate("method-not-implemented-title"),
                translator.translate("method-not-implemented")
        );
    }

    static public <T> T toDo(String title, String message) {
        throw new NotImplementedException(title, message);
    }
}
