package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent;
import ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type;

import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.CHANNEL;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.ADD_USER;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.BLOCKED;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.DROP_MESSAGE;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.EDIT_MESSAGE;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.KICK_USER;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.NAME_CHANGED;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.NEW_MESSAGE;
import static ru.bobrov.vyacheslav.chat.services.websocket.events.ChatEvent.Type.UNBLOCKED;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@NonNull
@Slf4j
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
        log.info(format("Send message type: %s to channel %s", type, format(CHANNEL, uuid)));
        messagingTemplate.convertAndSend(format(CHANNEL, uuid), ChatEvent.builder().type(type).uuid(uuid).build());
    }
}
