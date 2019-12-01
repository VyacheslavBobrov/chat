package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ResourceExistsException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.UserNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.*;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserRole.USER;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.Utils.*;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Transactional
public class UserService {
    @NonNull UserRepository repository;
    @NonNull PasswordEncoder bCryptEncoder;

    public User get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(UserNotFoundException::new);
    }

    public List<User> get(Iterable<UUID> userUUIDs) {
        ArrayList<User> users = new ArrayList<>();
        repository.findAllById(userUUIDs).forEach(users::add);
        return Collections.unmodifiableList(users);
    }

    public User create(String name, String login, String password) {
        User user = User.builder()
                .userId(UUID.randomUUID())
                .name(name)
                .login(login)
                .password(bCryptEncoder.encode(password))
                .role(USER)
                .status(ACTIVE)
                .build();

        initTime(user);
        validate(user);

        if (exists(login))
            throw new ResourceExistsException(format("User login: %s - is exists", login));

        return repository.save(user);
    }

    public User update(UUID uuid, String name, String login, String password) {
        User user = get(uuid);

        if (name == null && login == null && password == null)
            return user;

        boolean needToSave = false;

        if (!isBlank(name)) {
            needToSave = true;
            user.setName(name);
        }

        if (!isBlank(login)) {
            needToSave = true;
            user.setLogin(login);
        }

        if (!isBlank(password)) {
            needToSave = true;
            user.setPassword(bCryptEncoder.encode(password));
        }

        if (!needToSave)
            return user;

        updateTime(user);
        validate(user);
        return repository.save(user);
    }

    public User block(UUID uuid) {
        return setUserStatus(uuid, DISABLED);
    }

    public User unblock(UUID uuid) {
        return setUserStatus(uuid, ACTIVE);
    }

    public Set<Chat> getUserChats(UUID uuid) {
        User user = get(uuid);
        return user.getChats();
    }

    private User setUserStatus(UUID uuid, UserStatus status) {
        User user = get(uuid);
        user.setStatus(status);
        updateTime(user);
        repository.save(user);

        return user;
    }

    public boolean exists(String login) {
        return !repository.findAllByLogin(login).isEmpty();
    }

    private void validate(User user) {
        assertNotNull(user.getUserId(), "User id is null");
        assertNotBlank(user.getLogin(), "User login is null or blank");
        assertNotBlank(user.getName(), "User name is null or blank");
        assertNotBlank(user.getPassword(), "User password is null or blank");
        assertNotNull(user.getRole(), "User role is null");
        assertNotNull(user.getStatus(), "User status is null");
        checkTimeInfo(user);
    }

    public Page<User> getAllUsers(int page, int size) {
        return repository.findAllByStatus(ACTIVE, PageRequest.of(page, size));
    }

    public User getUserByLogin(String username) {
        List<User> users = repository.findAllByLogin(username);
        return users.isEmpty() ? null : users.get(0);
    }
}
