package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.ChatService;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatListEvent.Type;

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
    ChatService chatService;

    public void newChat(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getLogin(), NEW_CHAT, chatId));
    }

    public void chatBlocked(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getLogin(), BLOCKED_CHAT, chatId));
    }

    public void chatUnblocked(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getLogin(), UNBLOCKED_CHAT, chatId));
    }

    public void nameChanged(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getLogin(), RENAMED_CHAT, chatId));
    }

    public void newMessageInChat(UUID chatId) {
        chatService.applyForChatUsers(chatId, user -> sendMessage(user.getLogin(), NEW_MESSAGE, chatId));
    }

    private void sendMessage(String login, Type type, UUID uuid) {
        messagingTemplate.convertAndSendToUser(
                login,
                CHANNEL,
                ChatListEvent.builder().uuid(uuid).type(type).build()
        );
    }
}
