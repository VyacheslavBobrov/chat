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
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.IllegalOperationException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.UserNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.ChatRepository;

import javax.transaction.Transactional;
import java.util.*;

import static java.lang.String.format;
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
@NonNull
public class ChatService {
    ChatRepository repository;
    UserService userService;
    Translator translator;

    /**
     * Получить чат по идентификатору
     *
     * @param uuid уникальный идентификатор чата
     * @return {@link Chat} созданный чат
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
     */
    public Chat get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(() -> new ChatNotFoundException(
                translator.translate("chat-not-found-title"),
                format(translator.translate("chat-not-found"), uuid)
        ));
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
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
     */
    public Chat update(UUID chatId, String title) {
        Chat chat = get(chatId);
        chat.setTitle(title);
        updateTime(chat);
        validate(chat);
        return repository.save(chat);
    }

    /**
     * Добавить пользователей в чат
     *
     * @param chatId    {@link UUID} идентификатор чата
     * @param userUUIDs {@link List<UUID>} список идентификаторов пользователей
     * @return {@link Set<User>} пользователи, добавленные в чат
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
     * @throws UserNotFoundException пользователь с переданным идентификатором, отсутствует
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
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
     */
    public Chat block(UUID uuid) {
        return setChatStatus(uuid, ChatStatus.DISABLED);
    }

    /**
     * Разблокировать чат
     *
     * @param uuid {@link UUID} идентификатор чата
     * @return {@link Chat} разблокированный чат
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
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
     * @throws ChatNotFoundException чат с указанным идентификатором отсутствует
     */
    public Set<User> getChatUsers(UUID uuid) {
        return get(uuid).getUsers();
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

    /**
     * Убрать пользователя из чата
     *
     * @param chatId {@link UUID} идентификатор чата
     * @param userId {@link UUID} идентификатор пользователя
     * @return {@link Set<User>} новый список пользователей
     */
    public Set<User> kickUser(UUID chatId, UUID userId) {
        Chat chat = get(chatId);
        if (chat.getCreator().getUserId().equals(userId))
            throw new IllegalOperationException(
                    translator.translate("wrong-kick-operation-title"),
                    format(translator.translate("wrong-kick-operation"),
                            chat.getCreator().getLogin(),
                            chat.getTitle()
                    )
            );

        Set<User> users = chat.getUsers();
        User userToKick = users.stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);
        if (userToKick == null)
            return users;

        users.remove(userToKick);
        repository.save(chat);
        return users;
    }

    /**
     * Добавить сообщение в чат
     *
     * @param chatId  {@link UUID} идентификатор чата
     * @param message {@link Message} Сообщение
     */
    public void addMessageToChat(UUID chatId, Message message) {
        Chat chat = get(chatId);
        chat.getMessages().add(message);
        updateTime(chat);
        repository.save(chat);
    }
}
