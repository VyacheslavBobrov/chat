package ru.bobrov.vyacheslav.chat.testdata;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.dataproviders.entities.MessageStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.MessageStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_CREATE_MESSAGE_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Chats.TEST_FOR_GET_MESSAGES_CHAT;
import static ru.bobrov.vyacheslav.chat.testdata.Users.*;

public class Messages {
    public static final Set<Message> MESSAGES = Set.of(
            Message.builder()
                    .user(TEST_USER_1)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Всем привет в этом чате")
                    .status(ACTIVE)
                    .build(),
            Message.builder()
                    .user(TEST_USER_2)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Здрасте")
                    .status(ACTIVE)
                    .build(),
            Message.builder()
                    .user(TEST_USER_3)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Кто здесь?")
                    .status(ACTIVE)
                    .build(),
            Message.builder()
                    .user(TEST_USER_4)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Молодая, динамично развивающаяся компания \"ООО Торгинвестканцстройпром\"," +
                            " предлагает широкий выбор продукции. Самовывоз из Воркуты. ")
                    .status(DISABLED)
                    .build(),
            Message.builder()
                    .user(TEST_USER_5)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Хватит нас спамить!")
                    .status(ACTIVE)
                    .build(),
            Message.builder()
                    .user(TEST_ADMIN)
                    .chat(TEST_FOR_GET_MESSAGES_CHAT)
                    .message("Спамеры будут уничтожаться!")
                    .status(ACTIVE)
                    .build()
    );
    public static final Map<Message, UUID> MESSAGE_UUID_MAP = new HashMap<>();

    public static Message TEST_FOR_CREATE_MESSAGE = Message.builder()
            .user(TEST_USER_1)
            .chat(TEST_FOR_CREATE_MESSAGE_CHAT)
            .message("Всем привет в этом чате")
            .status(ACTIVE)
            .build();
}
