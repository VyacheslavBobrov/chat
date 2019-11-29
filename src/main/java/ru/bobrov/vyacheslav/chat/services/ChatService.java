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

    public Chat create(Chat chat) {
        chat.setChatId(UUID.randomUUID());
        initTime(chat);
        validate(chat);
        checkUsers(chat);

        return repository.save(chat);
    }

    public void update(Chat chat) {
        updateTime(chat);
        validate(chat);
        checkUsers(chat);
        repository.save(chat);
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

    private void checkUsers(Chat chat) {
        chat.getUsers().stream().map(User::getUserId).forEach(userID -> userService.get(userID));
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
