package ru.bobrov.vyacheslav.chat.testdata;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.ChatStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_ADMIN;
import static ru.bobrov.vyacheslav.chat.testdata.Users.TEST_USER;

public class Chats {
    public static final Set<Chat> CHATS = Set.of(
            Chat.builder()
                    .chatId(UUID.randomUUID())
                    .title("Уютный чат")
                    .creator(TEST_USER)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(ACTIVE)
                    .created(Timestamp.valueOf(LocalDateTime.now()))
                    .updated(Timestamp.valueOf(LocalDateTime.now()))
                    .build(),
            Chat.builder()
                    .chatId(UUID.randomUUID())
                    .title("Флудильня")
                    .creator(TEST_USER)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(ACTIVE)
                    .created(Timestamp.valueOf(LocalDateTime.now()))
                    .updated(Timestamp.valueOf(LocalDateTime.now()))
                    .build(),
            Chat.builder()
                    .chatId(UUID.randomUUID())
                    .title("Опрометчиво созданный чат")
                    .creator(TEST_ADMIN)
                    .users(Set.of(TEST_USER, TEST_ADMIN))
                    .status(DISABLED)
                    .created(Timestamp.valueOf(LocalDateTime.now()))
                    .updated(Timestamp.valueOf(LocalDateTime.now()))
                    .build()
    );
}
