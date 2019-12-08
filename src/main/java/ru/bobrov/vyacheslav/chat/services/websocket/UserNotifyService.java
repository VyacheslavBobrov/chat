package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.services.dto.ChatUser;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@NonNull
public class UserNotifyService {
    SimpMessagingTemplate messagingTemplate;

    private ChatUser currentUser() {
        return (ChatUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }
}
