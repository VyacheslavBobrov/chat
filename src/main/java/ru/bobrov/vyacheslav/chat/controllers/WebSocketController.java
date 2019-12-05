package ru.bobrov.vyacheslav.chat.controllers;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.bobrov.vyacheslav.chat.controllers.models.request.CreateMessageApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.MessageApiModel;
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

    @MessageMapping("/chat/{chatId}/send")
    @SendTo("/chat/{chatId}")
    MessageApiModel sendMessage(@DestinationVariable UUID chatId, @Payload CreateMessageApiModel message, Principal user) {
        log.info(format("POST create message request, chatId: %s, user: %s, message: %s ",
                chatId, user.getName(), message.getMessage()));
        return toApi(messageService.create(chatId, message.getUserId(), message.getMessage()));
    }
}
