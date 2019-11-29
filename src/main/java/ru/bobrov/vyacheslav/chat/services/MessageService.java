package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.MessageStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.MessageNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.MessageRepository;

import javax.transaction.Transactional;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Utils.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Transactional
public class MessageService {
    @NonNull MessageRepository repository;
    @NonNull UserService userService;
    @NonNull ChatService chatService;

    public Message get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(MessageNotFoundException::new);
    }

    public Message create(Message message) {
        initTime(message);
        message.setMessageId(UUID.randomUUID());
        validate(message);
        message.setUser(userService.get(message.getUser().getUserId()));
        message.setChat(chatService.get(message.getChat().getChatId()));
        return repository.save(message);
    }

    public Message update(Message message) {
        updateTime(message);
        validate(message);
        return repository.save(message);
    }

    public Message block(UUID uuid) {
        return setMessageStatus(uuid, MessageStatus.DISABLED);
    }

    public Message unblock(UUID uuid) {
        return setMessageStatus(uuid, MessageStatus.ACTIVE);
    }

    private Message setMessageStatus(UUID uuid, MessageStatus status) {
        Message message = get(uuid);
        updateTime(message);
        message.setStatus(status);
        return repository.save(message);
    }

    private void validate(Message message) {
        assertNotNull(message.getMessageId(), "Message id is null");
        assertNotNull(message.getUser(), "Message user is null");
        assertNotNull(message.getUser().getUserId(), "Message user id is null");
        assertNotNull(message.getChat(), "Message chat is null");
        assertNotNull(message.getChat().getChatId(), "Message chat id is null");
        assertNotBlank(message.getMessage(), "Message text is null or blank");
        assertNotNull(message.getStatus(), "Message status is null");
        checkTimeInfo(message);
    }
}
