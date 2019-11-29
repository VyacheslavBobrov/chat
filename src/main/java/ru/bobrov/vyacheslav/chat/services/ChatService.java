package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ChatNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.ChatRepository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Utils.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Transactional
public class ChatService {
    @NonNull ChatRepository repository;
    @NonNull UserService userService;

    public Chat get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(ChatNotFoundException::new);
    }

    public Chat create(
            String name,
            UUID creator
    ) {
        User user = userService.get(creator);

        Chat chat = Chat.builder()
                .chatId(UUID.randomUUID())
                .name(name)
                .status(ChatStatus.ACTIVE)
                .creator(user)
                .users(Collections.singleton(user))
                .build();

        initTime(chat);
        validate(chat);

        return repository.save(chat);
    }

    public Chat update(UUID chatId, String name) {
        Chat chat = get(chatId);
        chat.setName(name);
        updateTime(chat);
        validate(chat);
        return repository.save(chat);
    }

    public Chat addUsers(UUID chatId, Collection<UUID> userUUIDs) {
        Chat chat = get(chatId);
        chat.getUsers().addAll(userService.get(userUUIDs));
        return repository.save(chat);
    }

    public Chat block(UUID uuid) {
        return setChatStatus(uuid, ChatStatus.DISABLED);
    }

    public Chat unblock(UUID uuid) {
        return setChatStatus(uuid, ChatStatus.ACTIVE);
    }

    private Chat setChatStatus(UUID uuid, ChatStatus status) {
        Chat chat = get(uuid);
        chat.setStatus(status);
        updateTime(chat);
        repository.save(chat);
        return chat;
    }

    public Set<User> getChatUsers(UUID uuid) {
        return get(uuid).getUsers();
    }

    public Set<Message> getChatMessages(UUID uuid) {
        return get(uuid).getMessages();
    }

    private void validate(Chat chat) {
        assertNotNull(chat.getChatId(), "Chat id is null");
        assertNotNull(chat.getCreator(), "Chat creator is null");
        assertNotBlank(chat.getName(), "Chat name is null or blank");
        assertNotNull(chat.getStatus(), "Chat status is null");
        if (chat.getUsers() == null || chat.getUsers().isEmpty())
            throw new IllegalArgumentException("Chat users is null or empty");
        checkTimeInfo(chat);
    }
}
