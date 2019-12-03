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
    public boolean mayEditUser(@NonNull UserDetails principal, @NonNull UUID userId) {
        return isAdmin(principal) || isCurrentUserId(principal, userId);
    }

    public boolean mayGetChats(@NonNull UserDetails principal, @NonNull UUID userId) {
        return isAdmin(principal) || isCurrentUserId(principal, userId);
    }
}
