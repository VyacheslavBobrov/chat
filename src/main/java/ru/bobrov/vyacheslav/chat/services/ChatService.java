package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ChatNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.ChatRepository;

import javax.transaction.Transactional;
import java.util.*;

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
            String title,
            UUID creator
    ) {
        User user = userService.get(creator);

        Chat chat = Chat.builder()
                .chatId(UUID.randomUUID())
                .title(title)
                .status(ChatStatus.ACTIVE)
                .creator(user)
                .users(Collections.singleton(user))
                .build();

        initTime(chat);
        validate(chat);

        return repository.save(chat);
    }

    public Chat update(UUID chatId, String title) {
        Chat chat = get(chatId);
        chat.setTitle(title);
        updateTime(chat);
        validate(chat);
        return repository.save(chat);
    }

    public Chat update(UUID chatId) {
        Chat chat = get(chatId);
        updateTime(chat);
        return repository.save(chat);
    }

    public Set<User> addUsers(UUID chatId, Collection<UUID> userUUIDs) {
        Chat chat = get(chatId);
        chat.getUsers().addAll(userService.get(userUUIDs));
        repository.save(chat);
        return chat.getUsers();
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

    public Page<Message> getChatMessages(UUID uuid, int page, int size) {
        List<Message> messages = new ArrayList<>(get(uuid).getMessages());
        messages.sort(Comparator.comparing(Message::getCreated).reversed());

        return new PageImpl<>(messages, PageRequest.of(page, size), messages.size());
    }

    private void validate(Chat chat) {
        assertNotNull(chat.getChatId(), "Chat id is null");
        assertNotNull(chat.getCreator(), "Chat creator is null");
        assertNotBlank(chat.getTitle(), "Chat title is null or blank");
        assertNotNull(chat.getStatus(), "Chat status is null");
        if (chat.getUsers() == null || chat.getUsers().isEmpty())
            throw new IllegalArgumentException("Chat users is null or empty");
        checkTimeInfo(chat);
    }
}
