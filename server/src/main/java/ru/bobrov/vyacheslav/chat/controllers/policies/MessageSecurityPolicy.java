package ru.bobrov.vyacheslav.chat.controllers.policies;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.services.ChatService;
import ru.bobrov.vyacheslav.chat.services.MessageService;

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
public class MessageSecurityPolicy {
    MessageService messageService;
    ChatService chatService;

    public boolean canUpdateMessage(@NonNull UserDetails principal, @NonNull UUID messageId) {
        //Сообщение может редактировать его создатель и админ
        return isAdmin(principal) || isMessageCreator(principal, messageId);
    }

    public boolean canCreateMessage(@NonNull UserDetails principal, @NonNull UUID chatId, @NonNull UUID userId) {
        //Сообщение можно создавать только от своего имени, админ может создавать сообщения везде,
        // пользователь только в том чате, в который он добавлен
        return isCurrentUserId(principal, userId) && (isAdmin(principal) || userInChat(principal, chatId));
    }

    public boolean canReadMessage(@NonNull UserDetails principal, @NonNull UUID messageId) {
        val message = messageService.get(messageId);
        //Сообщение может читать админ, и пользователь чата, которому принадлежит сообщение
        return isAdmin(principal) || userInChat(principal, message.getChat().getChatId());
    }

    private boolean userInChat(UserDetails principal, UUID chatId) {
        return chatService.getChatUsers(chatId).stream().anyMatch(user -> isCurrentUserId(principal, user.getUserId()));
    }

    private boolean isMessageCreator(UserDetails principal, UUID messageId) {
        val message = messageService.get(messageId);
        return isCurrentUserId(principal, message.getUser().getUserId());
    }
}
