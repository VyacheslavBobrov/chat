package ru.bobrov.vyacheslav.chat.controllers.policies;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.services.UserService;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isAdmin;
import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isCurrentUserId;

@Component
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@NonNull
@SuppressWarnings("unused")
public class UserFilesSecurityPolicy {
    UserService userService;

    public boolean canUploadFile(@NonNull UserDetails principal, @NonNull UUID userId) {
        //Загружать файлы можно только от своего имени
        return isCurrentUserId(principal, userId);
    }

    public boolean canGetFilesIdsForUser(@NonNull UserDetails principal, @NonNull UUID userId) {
        //Список файлов можно получить только для себя, админ может получить списки любого пользователя
        return isCurrentUserId(principal, userId) || isAdmin(principal);
    }

    public boolean canDropFile(@NonNull UserDetails principal, @NonNull UUID fileId) {
        val user = userService.findUserByFileId(fileId);
        //Можно удалять только свои файлы, админ может удалить любой файл
        return isCurrentUserId(principal, user.getUserId()) || isAdmin(principal);
    }
}
