package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ChatNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.MessageNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.UserNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.MessageRepository;
import ru.bobrov.vyacheslav.chat.dto.enums.MessageStatus;
import ru.bobrov.vyacheslav.chat.services.utils.Translator;

import javax.transaction.Transactional;
import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.assertNotBlank;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.assertNotNull;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.checkTimeInfo;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.initTime;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.updateTime;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Transactional
@NonNull
public class MessageService {
    MessageRepository repository;
    UserService userService;
    ChatService chatService;
    Translator translator;

    /**
     * Получить сообщение по идентификатору
     *
     * @param uuid {@link UUID} идентификатор сообщения
     * @return {@link Message} найденное сообщение
     * @throws MessageNotFoundException сообщение с указанным идентификатором не найдено
     */
    public Message get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(() -> new MessageNotFoundException(
                translator.translate("message-not-found-title"),
                format(translator.translate("message-not-found"), uuid)
        ));
    }

    /**
     * Создать новое сообщение
     *
     * @param chatId {@link UUID} идентификатор чата, в котором будет создано сообщение
     * @param userId {@link UUID} идентификатор пользователя, от имени которого создается сообщение
     * @param text   текст сообщения
     * @return {@link Message} созданное сообщение
     * @throws ChatNotFoundException чат с указа4нным идентификатором не найден
     * @throws UserNotFoundException пользователь с указа4нным идентификатором не найден
     */
    public Message create(
            UUID chatId,
            UUID userId,
            String text
    ) {
        val message = Message.builder()
                .messageId(UUID.randomUUID())
                .chat(chatService.get(chatId))
                .user(userService.get(userId))
                .message(text)
                .status(MessageStatus.ACTIVE)
                .build();

        initTime(message);
        validate(message);
        return repository.save(message);
    }

    /**
     * Обновить сообщение
     *
     * @param messageId {@link UUID} идентификатор сообщения
     * @param text      текст сообщения
     * @return {@link Message} измененное сообщение
     * @throws MessageNotFoundException сообщение с указанным идентификатором не найдено
     */
    public Message update(UUID messageId, String text) {
        val message = get(messageId);
        message.setMessage(text);
        updateTime(message);
        validate(message);
        return repository.save(message);
    }

    /**
     * Блокировать сообщение
     *
     * @param uuid {@link UUID} идентификатор сообщения
     * @return {@link Message} заблокированное сообщение
     * @throws MessageNotFoundException сообщение с указанным идентификатором не найдено
     */
    public Message block(UUID uuid) {
        return setMessageStatus(uuid, MessageStatus.DISABLED);
    }

    /**
     * Разлокировать сообщение
     *
     * @param uuid {@link UUID} идентификатор сообщения
     * @return {@link Message} разблокированное сообщение
     * @throws MessageNotFoundException сообщение с указанным идентификатором не найдено
     */
    public Message unblock(UUID uuid) {
        return setMessageStatus(uuid, MessageStatus.ACTIVE);
    }

    /**
     * Получить сообщения чата (с пагинацией)
     *
     * @param chatId {@link UUID} идентификатор чата
     * @param page   номер страницы
     * @param size   размер страницы
     * @return {@link Page <Message>} порция сообщений чата
     * @throws ChatNotFoundException чат с указанным идентификатором не найден
     */
    public Page<Message> getChatMessages(UUID chatId, int page, int size) {
        val chat = chatService.get(chatId);
        return repository.findAllByChatAndStatusOrderByCreatedDesc(chat, MessageStatus.ACTIVE, PageRequest.of(page, size));
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
