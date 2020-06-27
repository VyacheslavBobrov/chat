package ru.bobrov.vyacheslav.chat.services.websocket.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@Builder
@NonNull
public class ChatEvent {
    public static final String CHANNEL = "/chat/%s";
    UUID uuid;
    Type type;
    Date timestamp = new Date();

    String name = "ChatEvent";

    public enum Type {
        NEW_MESSAGE,
        EDIT_MESSAGE,
        DROP_MESSAGE,

        ADD_USER,
        KICK_USER,

        NAME_CHANGED,

        BLOCKED,
        UNBLOCKED
    }
}
