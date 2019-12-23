package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.dto.response.MessageApiModel;
import ru.bobrov.vyacheslav.chat.services.MessageService;
import ru.bobrov.vyacheslav.chat.services.websocket.ChatListNotifyService;
import ru.bobrov.vyacheslav.chat.services.websocket.ChatNotifyService;

import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.MessagesDataConverter.toApi;

@Api("Chats messages management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/message")
@FieldDefaults(level = PRIVATE)
@Slf4j
@CrossOrigin
@NonNull
public class MessageController {
    MessageService messageService;
    ChatListNotifyService chatListNotifyService;
    ChatNotifyService chatNotifyService;

    @PreAuthorize("@messageSecurityPolicy.canReadMessage(principal, #messageId)")
    @ApiOperation(value = "Get message by uuid", response = MessageApiModel.class)
    @GetMapping("/{messageId}")
    public MessageApiModel get(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET message request from %s, messageId:%s ", header.getHost(), messageId));
        return toApi(messageService.get(messageId));
    }

    @PreAuthorize("@messageSecurityPolicy.canUpdateMessage(principal, #messageId)")
    @ApiOperation(value = "Update message data", response = MessageApiModel.class)
    @PostMapping("/{messageId}")
    public MessageApiModel update(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestParam String message,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST message update request from %s, messageId:%s, message: %s ",
                header.getHost(), messageId, message));
        MessageApiModel updatedMessage = toApi(messageService.update(messageId, message));
        chatNotifyService.editMessage(updatedMessage.getChat().getChatId());
        return updatedMessage;
    }

    @PreAuthorize("@messageSecurityPolicy.canUpdateMessage(principal, #messageId)")
    @ApiOperation(value = "Block message by uuid", response = MessageApiModel.class)
    @PostMapping("/{messageId}/block")
    public MessageApiModel block(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT block message request from %s, messageId:%s ", header.getHost(), messageId));
        MessageApiModel blockedMessage = toApi(messageService.block(messageId));
        chatNotifyService.dropMessage(blockedMessage.getChat().getChatId());
        return blockedMessage;
    }

    @PreAuthorize("@messageSecurityPolicy.canUpdateMessage(principal, #messageId)")
    @ApiOperation(value = "Unblock message by uuid", response = MessageApiModel.class)
    @PostMapping("/{messageId}/unblock")
    public MessageApiModel unblock(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT unblock message request from %s, messageId:%s ", header.getHost(), messageId));
        MessageApiModel unblockedMessage = toApi(messageService.unblock(messageId));
        chatNotifyService.editMessage(unblockedMessage.getChat().getChatId());
        return unblockedMessage;
    }

    @PreAuthorize("@messageSecurityPolicy.canCreateMessage(principal, #chatId, #userId)")
    @ApiOperation(value = "Create message", response = MessageApiModel.class)
    @PostMapping
    public MessageApiModel create(
            @RequestParam UUID chatId,
            @RequestParam UUID userId,
            @RequestParam String message,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST create message request from %s, chatId: %s, userId: %s, message: %s ",
                header.getHost(), chatId, userId, message));
        MessageApiModel createdMessage = toApi(messageService.create(chatId, userId, message));
        chatListNotifyService.newMessageInChat(chatId);
        chatNotifyService.newMessage(chatId);
        return createdMessage;
    }
}
