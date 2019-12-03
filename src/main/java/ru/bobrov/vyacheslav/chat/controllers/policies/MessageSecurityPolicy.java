package ru.bobrov.vyacheslav.chat.controllers.policies;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.services.ChatService;
import ru.bobrov.vyacheslav.chat.services.MessageService;

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
public class MessageSecurityPolicy {
    MessageService messageService;
    ChatService chatService;

    public boolean mayUpdateMessage(@NonNull UserDetails principal, @NonNull UUID messageId) {
        return isAdmin(principal) || isMessageCreator(principal, messageId);
    }

    public boolean mayCreateMessage(@NonNull UserDetails principal, @NonNull UUID chatId, @NonNull UUID userId) {
        return isCurrentUserId(principal, userId) && (isAdmin(principal) || userInChat(principal, chatId));
    }

    public boolean mayReadMessage(@NonNull UserDetails principal, @NonNull UUID messageId) {
        Message message = messageService.get(messageId);
        return isAdmin(principal) || userInChat(principal, message.getChat().getChatId());
    }

    private boolean userInChat(UserDetails principal, UUID chatId) {
        return chatService.getChatUsers(chatId).stream().anyMatch(user -> isCurrentUserId(principal, user.getUserId()));
    }

    private boolean isMessageCreator(UserDetails principal, UUID messageId) {
        Message message = messageService.get(messageId);
        return isCurrentUserId(principal, message.getUser().getUserId());
    }
}
