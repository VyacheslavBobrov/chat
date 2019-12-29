package ru.bobrov.vyacheslav.chat.services.websocket.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;

@Value
@Builder
@NonNull
public class UserEvent {
    public static final String CHANNEL = "/user";
    Type type;
    Date timestamp = new Date();

    final String name = "UserEvent";

    public enum Type {
        BLOCKED,
        UNBLOCKED,
        UPDATED,
        TOKEN_EXPIRED
    }
}
