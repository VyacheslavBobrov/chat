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

    public boolean mayBlockOrUnblockChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        return isAdmin(principal) || isChatCreator(principal, chatId);
    }

    public boolean mayReadChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        return isAdmin(principal) || chatService.getChatUsers(chatId)
                .stream().anyMatch(user -> isCurrentUserId(principal, user.getUserId()));
    }

    public boolean mayUpdateChat(@NonNull UserDetails principal, @NonNull UUID chatId) {
        return isAdmin(principal) || isChatCreator(principal, chatId);
    }

    public boolean isChatCreator(UserDetails principal, UUID chatId) {
        Chat chat = chatService.get(chatId);
        return isCurrentUserId(principal, chat.getCreator().getUserId());
    }
}
