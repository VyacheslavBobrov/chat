package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.Type;

import java.util.Date;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.CHANNEL;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.Type.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class ChatListNotifyService {
    SimpMessagingTemplate messagingTemplate;

    public void newChat(UUID chatId) {
        sendMessage(NEW_CHAT, chatId);
    }

    public void chatBlocked(UUID chatId) {
        sendMessage(BLOCKED_CHAT, chatId);
    }

    public void chatUnblocked(UUID chatId) {
        sendMessage(UNBLOCKED_CHAT, chatId);
    }

    public void chatRenamed(UUID chatId) {
        sendMessage(RENAMED_CHAT, chatId);
    }

    public void newMessageInChat(UUID chatId) {
        sendMessage(NEW_MESSAGE, chatId);
    }

    private void sendMessage(Type type, UUID uuid) {
        messagingTemplate.convertAndSend(
                CHANNEL,
                ChatListEvent.builder().uuid(uuid).type(type).timestamp(new Date()).build()
        );
    }
}
