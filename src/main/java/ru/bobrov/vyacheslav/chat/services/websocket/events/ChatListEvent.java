package ru.bobrov.vyacheslav.chat.services.websocket.events;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;
import java.util.UUID;

@Value
@Builder
@NonNull
public class ChatListEvent {
    public static final String CHANNEL = "/chat";
    UUID uuid;
    Type type;
    Date timestamp = new Date();

    public enum Type {
        NEW_CHAT,
        BLOCKED_CHAT,
        UNBLOCKED_CHAT,
        RENAMED_CHAT,
        NEW_MESSAGE
    }
}
