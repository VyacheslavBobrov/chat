package ru.bobrov.vyacheslav.chat.dataproviders.entities;

import java.sql.Timestamp;

public interface EntityWithTimeInfo {
    Timestamp getCreated();

    void setCreated(Timestamp time);

    Timestamp getUpdated();

    void setUpdated(Timestamp time);
}
