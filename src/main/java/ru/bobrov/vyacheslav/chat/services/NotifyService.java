package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.controllers.models.response.ChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.MessageApiModel;
import ru.bobrov.vyacheslav.chat.services.dto.ChatUser;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class NotifyService {
    SimpMessagingTemplate messagingTemplate;

    public void notifyUser(String message) {
        ChatUser chatUser = (ChatUser) SecurityContextHolder.getContext().getAuthentication().getDetails();

        messagingTemplate.convertAndSendToUser(chatUser.getUsername(), "/user/" + chatUser.getId(), message);
    }

    public void notifyUsers(ChatApiModel chat) {
        messagingTemplate.convertAndSend(format("/chat/%s", chat.getChatId()), chat);
    }

    public void notifyUsers(MessageApiModel message) {
        messagingTemplate.convertAndSend(format("/chat/%s/messages", message.getChat().getChatId()), message);
    }
}
