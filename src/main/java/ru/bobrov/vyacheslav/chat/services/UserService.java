package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.*;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.FileNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ResourceExistsException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.UserNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserFileRepository;
import ru.bobrov.vyacheslav.chat.dataproviders.repositories.UserRepository;
import ru.bobrov.vyacheslav.chat.services.utils.Translator;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserRole.USER;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.ACTIVE;
import static ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus.DISABLED;
import static ru.bobrov.vyacheslav.chat.services.utils.Utils.*;

/**
 * Сервис для работы с пользователями чатов
 */
@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Transactional
@NonNull
public class UserService {
    UserRepository repository;
    UserFileRepository userFileRepository;

    PasswordEncoder bCryptEncoder;
    Translator translator;

    /**
     * Получить пользователя по идентификатору
     *
     * @param uuid {@link UUID} идентификатор пользователя
     * @return {@link User} найденный пользователь
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
    public User get(UUID uuid) {
        return repository.findById(uuid).orElseThrow(() -> new UserNotFoundException(
                translator.translate("user-not-found-title"),
                format(translator.translate("user-not-found"), uuid)
        ));
    }

    /**
     * Получить пользователей с указанными идентификаторами
     *
     * @param userUUIDs {@link Iterable<UUID>} идентификаторы пользователей
     * @return {@link List<User>} список найденных пользователей
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
    public List<User> get(Iterable<UUID> userUUIDs) {
        return StreamSupport
                .stream(repository.findAllById(userUUIDs).spliterator(), false)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Создать нового пользователя
     *
     * @param name     имя ползователя
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return {@link User} созданный пользователь
     * @throws ResourceExistsException  пользователь с указанным логином уе существует
     * @throws IllegalArgumentException одно из переданных полей нулевое или пустое
     */
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
            throw new ResourceExistsException(
                    translator.translate("user-login-exists-title"),
                    format(translator.translate("user-login-exists"), login)
            );

        return repository.save(user);
    }

    /**
     * Обновить данные пользователя
     *
     * @param uuid     {@link UUID} идентификатор пользователя
     * @param name     имя ползователя
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return {@link User} обновленный пользователь
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
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

    /**
     * Заблокировать пользователя
     *
     * @param uuid {@link UUID} идентификатор пользователя
     * @return {@link User} заблокированный пользователь
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User block(UUID uuid) {
        return setUserStatus(uuid, DISABLED);
    }

    /**
     * Разблокировать пользователя
     *
     * @param uuid {@link UUID} идентификатор пользователя
     * @return {@link User} разблокированный пользователь
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User unblock(UUID uuid) {
        return setUserStatus(uuid, ACTIVE);
    }

    private User setUserStatus(UUID uuid, UserStatus status) {
        User user = get(uuid);
        user.setStatus(status);
        updateTime(user);
        repository.save(user);

        return user;
    }

    /**
     * Получить все чаты пользователя
     *
     * @param uuid {@link UUID} идентификатор пользователя
     * @return {@link Set<Chat>} список чатов пользователя
     * @throws UserNotFoundException пользователь с указанным идентификатором отсутствует
     */
    public Set<Chat> getUserChats(UUID uuid) {
        User user = get(uuid);
        return user.getChats().stream()
                .filter(chat -> chat.getStatus() == ChatStatus.ACTIVE)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Проверка существования пользователя по логину
     *
     * @param login логин пользователя
     * @return true, если пользователь существует
     */
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

    /**
     * Получить список всех активных пользователей
     *
     * @param page номер страницы
     * @param size размер страницы
     * @return {@link Page<User>} активные пользователи
     */
    public Page<User> getAllActiveUsers(int page, int size) {
        return repository.findAllByStatus(ACTIVE, PageRequest.of(page, size));
    }

    /**
     * Получить список активных пользователей не участвующих в указанном чате
     *
     * @param chatId {@link UUID} идентификатор чата
     * @return {@link List<User>} список найденных пользователей
     */
    public List<User> getAllActiveUsersOutOfChat(UUID chatId) {
        return repository.findAllByUserOutOfChatAndStatus(chatId, ACTIVE);
    }

    /**
     * Получить пользователя по его логину
     *
     * @param username логин
     * @return {@link User} найденный пользователь
     */
    public User getUserByLogin(String username) {
        List<User> users = repository.findAllByLogin(username);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Получить список идентификаторов файлов, для пользователя
     *
     * @param userId {@link UUID} идентификатор пользователя
     * @return {@link List<UUID>} список идентификаторов файлов
     */
    public List<UUID> getFilesIdsForUser(UUID userId) {
        User user = get(userId);
        return user.getUserFiles().stream().map(UserFile::getFileId).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Удалить файл пользователя
     *
     * @param fileId {@link UUID} идентификатор файла
     */
    public void deleteFile(UUID fileId) {
        userFileRepository.deleteById(fileId);
    }

    /**
     * Добавить файл для пользователя
     *
     * @param userId      {@link UUID} идентификатор пользователя
     * @param contentType MIME тип файла
     * @return {@link UUID} идентификатор созданного файла
     */
    public UUID addFileToUser(UUID userId, String contentType) {
        User user = get(userId);

        UUID fileId = UUID.randomUUID();
        UserFile userFile = UserFile.builder().fileId(fileId).user(user).fileMimeType(contentType).build();
        user.getUserFiles().add(userFile);
        repository.save(user);

        return fileId;
    }

    /**
     * Найти идентификатор пользователя, которому принадлежит файл
     *
     * @param fileId {@link UUID} идентификатор файла
     * @return {@link UUID} идентификатор пользователя
     */
    public User findUserByFileId(UUID fileId) {
        return userFileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(
                translator.translate("file-not-found-title"),
                format(translator.translate("file-not-found"), fileId)
        )).getUser();
    }

    /**
     * Найти файл
     *
     * @param fileId {@link UUID} идентификатор файла
     * @return {@link UserFile} найденный файл
     */
    public UserFile findFileById(UUID fileId) {
        return userFileRepository.findById(fileId).orElseThrow(() -> new FileNotFoundException(
                translator.translate("file-not-found-title"),
                format(translator.translate("file-not-found"), fileId)
        ));
    }
}
