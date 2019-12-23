package ru.bobrov.vyacheslav.chat.controllers.policies;

import lombok.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isAdmin;
import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isCurrentUserId;

@Component
@SuppressWarnings("unused")
public class UserSecurityPolicy {
    public boolean canEditUser(@NonNull UserDetails principal, @NonNull UUID userId) {
        //Пользователя может редактировать админ и сам пользователь
        return isAdmin(principal) || isCurrentUserId(principal, userId);
    }

    public boolean canGetChats(@NonNull UserDetails principal, @NonNull UUID userId) {
        //Чаты пользователя может читать админ и сам пользователь
        return isAdmin(principal) || isCurrentUserId(principal, userId);
    }
}
