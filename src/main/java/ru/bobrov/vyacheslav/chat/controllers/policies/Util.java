package ru.bobrov.vyacheslav.chat.controllers.policies;

import org.springframework.security.core.userdetails.UserDetails;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserRole;
import ru.bobrov.vyacheslav.chat.services.dto.ChatUser;

import java.util.UUID;

public class Util {
    public static boolean isAdmin(UserDetails principal) {
        return principal.getAuthorities()
                .stream().anyMatch(role -> role.getAuthority().equals("ROLE_" + UserRole.ADMIN.name()));
    }

    public static boolean isCurrentUserId(UserDetails principal, UUID userId) {
        return ((ChatUser) principal).getId().equals(userId);
    }
}
