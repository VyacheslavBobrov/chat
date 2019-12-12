package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.websocket.events.UserEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.UserEvent.Type;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.UserEvent.CHANNEL;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.UserEvent.Type.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class UserNotifyService {
    SimpMessagingTemplate messagingTemplate;

    public void blocked(UUID userId) {
        sendMessage(BLOCKED, userId);
    }

    public void unblocked(UUID userId) {
        sendMessage(UNBLOCKED, userId);
    }

    public void tokenExpired(UUID userId) {
        messagingTemplate.convertAndSendToUser(userId.toString(), CHANNEL, UserEvent.builder().type(TOKEN_EXPIRED).build());
    }

    private void sendMessage(Type type, UUID userId) {
        messagingTemplate.convertAndSendToUser(userId.toString(), CHANNEL, UserEvent.builder().type(type).build());
    }
}
