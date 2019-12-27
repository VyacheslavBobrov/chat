package ru.bobrov.vyacheslav.chat.testdata;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.dto.enums.UserRole.ADMIN;
import static ru.bobrov.vyacheslav.chat.dto.enums.UserRole.USER;
import static ru.bobrov.vyacheslav.chat.dto.enums.UserStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dto.enums.UserStatus.DISABLED;

public class Users {
    public static final User TEST_ADMIN = User.builder()
            .userId(UUID.fromString("4d6b8d54-3d13-40ed-978b-8e71a6abc58b"))
            .login("admin")
            .password("123456789")
            .name("ADMIN")
            .role(ADMIN)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();
    public static final User TEST_USER_1 = User.builder()
            .userId(UUID.randomUUID())
            .login("user1")
            .password("useruser")
            .name("Григорий Хачатурянович Айншлютц")
            .role(USER)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    public static final User TEST_USER_2 = User.builder()
            .userId(UUID.randomUUID())
            .login("user2")
            .password("useruser")
            .name("Апполинарий Иванович Бураченко")
            .role(USER)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    public static final User TEST_USER_3 = User.builder()
            .userId(UUID.randomUUID())
            .login("user3")
            .password("useruser")
            .name("Рафик Виссарионович Доброхотов")
            .role(USER)
            .status(ACTIVE)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    public static final User TEST_USER_4 = User.builder()
            .userId(UUID.randomUUID())
            .login("user4")
            .password("useruser")
            .name("Иван Павлович Рабинович")
            .role(USER)
            .status(DISABLED)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    public static final User TEST_USER_5 = User.builder()
            .userId(UUID.randomUUID())
            .login("user")
            .password("useruser")
            .name("Николай Иванович Розеншпиц")
            .role(USER)
            .status(DISABLED)
            .created(Timestamp.valueOf(LocalDateTime.now()))
            .updated(Timestamp.valueOf(LocalDateTime.now()))
            .build();

    public static final Set<User> ALL_USERS = Set.of(
            TEST_ADMIN,
            TEST_USER_1,
            TEST_USER_2,
            TEST_USER_3,
            TEST_USER_4,
            TEST_USER_5
    );
}
