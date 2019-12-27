package ru.bobrov.vyacheslav.chat.testdata;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.testdata.Users.*;

public class Chats {
    public static final Chat TEST_FOR_CREATE_CHAT = Chat.builder()
            .title("Уютный чат")
            .creator(TEST_USER_1)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_GET_CHAT = Chat.builder()
            .title("Чат")
            .creator(TEST_ADMIN)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_UPDATE_CHAT = Chat.builder()
            .title("Какое-то не особо удачное имя чата")
            .creator(TEST_ADMIN)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_ADD_USERS_CHAT = Chat.builder()
            .title("Шумный чат")
            .creator(TEST_ADMIN)
            .users(ALL_USERS)
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_GET_USERS_CHAT = Chat.builder()
            .title("Очень тихий чат")
            .creator(TEST_ADMIN)
            .users(ALL_USERS)
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_GET_USERS_OUT_OF_CHAT = Chat.builder()
            .title("Очень шумный чат")
            .creator(TEST_ADMIN)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_KICK_USER_OUT_OF_CHAT = Chat.builder()
            .title("Чат с лишними людьми")
            .creator(TEST_ADMIN)
            .users(ALL_USERS)
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_BLOCK_CHAT = Chat.builder()
            .title("Лишний чат")
            .creator(TEST_ADMIN)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_UNBLOCK_CHAT = Chat.builder()
            .title("Чат, который смог")
            .creator(TEST_ADMIN)
            .users(Set.of(TEST_USER_1, TEST_ADMIN))
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_GET_MESSAGES_CHAT = Chat.builder()
            .title("Чат, в который пишут")
            .creator(TEST_ADMIN)
            .users(ALL_USERS)
            .status(ACTIVE)
            .build();

    public static final Chat TEST_FOR_CREATE_MESSAGE_CHAT = Chat.builder()
            .title("Чат, c одним сообщением")
            .creator(TEST_USER_1)
            .users(Set.of(TEST_ADMIN, TEST_USER_1))
            .status(ACTIVE)
            .build();


    public static final Set<Chat> CHATS = Set.of(
            Chat.builder()
                    .title("Уютный чат")
                    .creator(TEST_USER_1)
                    .users(Set.of(TEST_USER_1, TEST_ADMIN))
                    .status(ACTIVE)
                    .build(),
            Chat.builder()
                    .title("Флудильня")
                    .creator(TEST_USER_1)
                    .users(Set.of(TEST_USER_1, TEST_ADMIN))
                    .status(ACTIVE)
                    .build(),
            Chat.builder()
                    .title("Опрометчиво созданный чат")
                    .creator(TEST_ADMIN)
                    .users(Set.of(TEST_USER_1, TEST_ADMIN))
                    .status(DISABLED)
                    .build()
    );

    public static final Map<Chat, UUID> CHAT_IDS_MAP = new HashMap<>(CHATS.size());
}
