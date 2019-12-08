package ru.bobrov.vyacheslav.chat.services.websocket.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;

@Value
@Builder
@NonNull
public class UserEvent {
    public static final String CHANNEL = "/user/%s";
    Type type;
    Date timestamp;

    public enum Type {
        BLOCKED,
        UNBLOCKED,
        TOKEN_EXPIRED
    }
}
