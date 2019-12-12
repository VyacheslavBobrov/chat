package ru.bobrov.vyacheslav.chat.testdata;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_ADMIN;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_USER;

public class Chats {
    public static final Set<Chat> CHATS = Set.of(
            Chat.builder()
                    .title("Уютный чат")
                    .creator(TEST_USER)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(ACTIVE)
                    .build(),
            Chat.builder()
                    .title("Флудильня")
                    .creator(TEST_USER)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(ACTIVE)
                    .build(),
            Chat.builder()
                    .title("Опрометчиво созданный чат")
                    .creator(TEST_ADMIN)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(DISABLED)
                    .build()
    );

    public static final Map<Chat, UUID> CHAT_IDS_MAP = new HashMap<>(CHATS.size());
}
