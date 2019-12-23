package ru.bobrov.vyacheslav.chat.controllers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import ru.bobrov.vyacheslav.chat.controllers.models.request.CreateMessageApiModel;
import ru.bobrov.vyacheslav.chat.dto.response.MessageApiModel;
import ru.bobrov.vyacheslav.chat.services.MessageService;

import java.security.Principal;
import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.MessagesDataConverter.toApi;

@Controller
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Slf4j
@NonNull
public class WebSocketController {
    MessageService messageService;

    @MessageMapping("/chat/")
    @SubscribeMapping("/chat")
    @SendTo("/chat")
    String subscribeChat() {
        return "Ыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыы";
    }

    @MessageMapping("/chat/{chatId}")
    @SubscribeMapping("/chat/{chatId}")
    void subscribe(@DestinationVariable UUID chatId, Principal user) {
        log.info(format("User: %s, subscribed to /chat/%s channel", user.getName(), chatId));
    }

    @MessageMapping("/chat/{chatId}/message/send")
    @SendTo("/chat/{chatId}/messages")
    MessageApiModel sendMessage(@DestinationVariable UUID chatId, @Payload CreateMessageApiModel message, Principal user) {
        log.info(format("POST create message request, chatId: %s, user: %s, message: %s ",
                chatId, user.getName(), message.getMessage()));
        return toApi(messageService.create(chatId, message.getUserId(), message.getMessage()));
    }
}
