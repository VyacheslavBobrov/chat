package ru.bobrov.vyacheslav.chat.controllers.policies;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.services.ChatService;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isAdmin;
import static ru.bobrov.vyacheslav.chat.controllers.policies.Util.isCurrentUserId;

@Component
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
@SuppressWarnings("unused")
public class ChatSecurityPolicy {
    ChatService chatService;

    public boolean canBlockOrUnblockChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        //Заблокировать и разблокировать чат может админ и создатель чата
        return isAdmin(principal) || isChatCreator(principal, chatId);
    }

    public boolean canReadChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        //Данные чата может читать админ и участники чата
        return isAdmin(principal) || chatService.getChatUsers(chatId)
                .stream().anyMatch(user -> isCurrentUserId(principal, user.getUserId()));
    }

    public boolean canUpdateChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        //Обновлять данные чата могут админ и создатель чата
        return isAdmin(principal) || isChatCreator(principal, chatId);
    }

    public boolean canKickUser(@NonNull UserDetails principal, @NonNull UUID chatId, @NonNull UUID userId) {
        //Из чата могут выбросить пользователя: админ, создатель чата, и сам пользователь.
        return isAdmin(principal) || isChatCreator(principal, chatId) || isCurrentUserId(principal, userId);
    }

    public boolean isChatCreator(UserDetails principal, UUID chatId) {
        Chat chat = chatService.get(chatId);
        return isCurrentUserId(principal, chat.getCreator().getUserId());
    }
}
