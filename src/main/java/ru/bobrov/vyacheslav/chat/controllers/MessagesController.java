package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.controllers.models.request.CreateMessageApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.MessageApiModel;
import ru.bobrov.vyacheslav.chat.services.MessageService;

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
public class MessagesController {
    @NonNull MessageService messageService;

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
        return toApi(messageService.update(messageId, message));
    }

    @ApiOperation(value = "Block message by uuid", response = MessageApiModel.class)
    @PutMapping("/{messageId}/block")
    public MessageApiModel block(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT block message request from %s, messageId:%s ", header.getHost(), messageId));
        return toApi(messageService.block(messageId));
    }

    @ApiOperation(value = "Unblock message by uuid", response = MessageApiModel.class)
    @PutMapping("/{messageId}/unblock")
    public MessageApiModel unblock(
            @ApiParam(value = "Message uuid", required = true)
            @PathVariable UUID messageId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT unblock message request from %s, messageId:%s ", header.getHost(), messageId));
        return toApi(messageService.unblock(messageId));
    }

    @ApiOperation(value = "Create message", response = MessageApiModel.class)
    @PostMapping
    public MessageApiModel create(
            @RequestBody CreateMessageApiModel request,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST create message request from %s, chatId: %s, userId: %s, message: %s ",
                header.getHost(), request.getChatId(), request.getUserId(), request.getMessage()));
        return toApi(messageService.create(request.getChatId(), request.getUserId(), request.getMessage()));
    }
}