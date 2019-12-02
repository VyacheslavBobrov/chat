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

/**
 * Сервис для работы с сущностью Chat
 */
@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Transactional
public class ChatService {
    @NonNull ChatRepository repository;
    @NonNull UserService userService;

    /**
     * Получить чат по идентификатору
     *
     * @param uuid уникальный идентификатор чата
     * @return {@link Chat} созданный чат
     */
    public Chat get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(ChatNotFoundException::new);
    }

    /**
     * Создать новый чат
     *
     * @param title   заголовок чата
     * @param creator {@link UUID} уникальный идентификатор создателя чата
     * @return {@link Chat} созданный чат
     */
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

    /**
     * Обновить данные чата
     *
     * @param chatId {@link UUID} идентификатор чата
     * @param title  Новый заголовок чата
     * @return {@link Chat} обновленный чат
     */
    public Chat update(UUID chatId, String title) {
        Chat chat = get(chatId);
        chat.setTitle(title);
        updateTime(chat);
        validate(chat);
        return repository.save(chat);
    }

    /**
     * Обновить время изменения чата (при добавлении нового сообщения)
     *
     * @param chatId {@link UUID} идентификатор чата
     */
    public void update(UUID chatId) {
        Chat chat = get(chatId);
        updateTime(chat);
        repository.save(chat);
    }

    /**
     * Добавить пользователей в чат
     *
     * @param chatId    {@link UUID} идентификатор чата
     * @param userUUIDs {@link List<UUID>} список идентификаторов пользователей
     * @return {@link Set<User>} пользователи, добавленные в чат
     */
    public Set<User> addUsers(UUID chatId, Collection<UUID> userUUIDs) {
        Chat chat = get(chatId);
        chat.getUsers().addAll(userService.get(userUUIDs));
        repository.save(chat);
        return chat.getUsers();
    }

    /**
     * Блокировать чат
     *
     * @param uuid {@link UUID} идентификатор чата
     * @return {@link Chat} заблокированный чат
     */
    public Chat block(UUID uuid) {
        return setChatStatus(uuid, ChatStatus.DISABLED);
    }

    /**
     * Разблокировать чат
     *
     * @param uuid {@link UUID} идентификатор чата
     * @return {@link Chat} разблокированный чат
     */
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

    /**
     * Получить всех пользователей чата
     *
     * @param uuid {@link UUID} идентификатор чата{@link UUID} идентификатор чата
     * @return {@link Set<User>} пользователи чата
     */
    public Set<User> getChatUsers(UUID uuid) {
        return get(uuid).getUsers();
    }

    /**
     * Получить сообщения чата (с пагинацией)
     *
     * @param uuid {@link UUID} идентификатор чата{@link UUID} идентификатор чата
     * @param page номер страницы
     * @param size размер страницы
     * @return {@link Page<Message>} порция сообщений чата
     */
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
