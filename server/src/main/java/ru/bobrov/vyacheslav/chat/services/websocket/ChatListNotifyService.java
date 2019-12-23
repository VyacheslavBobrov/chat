package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.ChatService;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.Type;

import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.CHANNEL;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.Type.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
@Slf4j
public class ChatListNotifyService {
    SimpMessagingTemplate messagingTemplate;
    ChatService chatService;

    public void newChatUser(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getUserId(), NEW_CHAT, chatId));
    }

    public void chatBlocked(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getUserId(), BLOCKED_CHAT, chatId));
    }

    public void chatUnblocked(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getUserId(), UNBLOCKED_CHAT, chatId));
    }

    public void nameChanged(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getUserId(), RENAMED_CHAT, chatId));
    }

    public void newMessageInChat(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getUserId(), NEW_MESSAGE, chatId));
    }

    private void sendMessage(UUID userId, Type type, UUID uuid) {
        log.info(format("Send message type: %s for %s, to channel %s", type, userId, CHANNEL));
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                CHANNEL,
                ChatListEvent.builder().uuid(uuid).type(type).build()
        );
    }
}
