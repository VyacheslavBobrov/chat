package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type;

import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.CHANNEL;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class ChatNotifyService {
    SimpMessagingTemplate messagingTemplate;

    public void newMessage(UUID chatId) {
        sendMessage(NEW_MESSAGE, chatId);
    }

    public void editMessage(UUID chatId) {
        sendMessage(EDIT_MESSAGE, chatId);
    }

    public void dropMessage(UUID chatId) {
        sendMessage(DROP_MESSAGE, chatId);
    }

    public void addUser(UUID chatId) {
        sendMessage(ADD_USER, chatId);
    }

    public void kickUser(UUID chatId) {
        sendMessage(KICK_USER, chatId);
    }

    public void nameChanged(UUID chatId) {
        sendMessage(NAME_CHANGED, chatId);
    }

    public void blocked(UUID chatId) {
        sendMessage(BLOCKED, chatId);
    }

    public void unblocked(UUID chatId) {
        sendMessage(UNBLOCKED, chatId);
    }

    private void sendMessage(Type type, UUID uuid) {
        messagingTemplate.convertAndSend(format(CHANNEL, ChatEvent.builder().type(type).uuid(uuid).build()));
    }
}
