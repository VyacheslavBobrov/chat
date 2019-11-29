package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.controllers.converters.MessagesDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.models.ChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.MessageApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.UserApiModel;
import ru.bobrov.vyacheslav.chat.services.ChatService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.ChatDataConverter.toApi;

@Api("Chats management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/chat")
@Slf4j
public class ChatsController {
    @NonNull ChatService chatService;

    @ApiOperation(value = "Get chat by uuid", response = ChatApiModel.class)
    @GetMapping("/{chatId}")
    public ChatApiModel get(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toApi(chatService.get(chatId));
    }

    @ApiOperation(value = "Update chat data", response = ChatApiModel.class)
    @PutMapping("/{chatId}")
    public ChatApiModel update(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @ApiParam(value = "Chat name", required = true)
            @RequestParam String name,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT update chat request from %s, chatId:%s, name: %s ", header.getHost(), chatId, name));
        return toApi(chatService.update(chatId, name));
    }

    @ApiOperation(value = "Block chat", response = ChatApiModel.class)
    @GetMapping("/{chatId}/block")
    public ChatApiModel block(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET block chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toApi(chatService.block(chatId));
    }

    @ApiOperation(value = "Unlock chat", response = ChatApiModel.class)
    @GetMapping("/{chatId}/unblock")
    public ChatApiModel unblock(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET unblock chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toApi(chatService.unblock(chatId));
    }

    @ApiOperation(value = "Create chat", response = ChatApiModel.class)
    @PutMapping
    public ChatApiModel create(
            @ApiParam(value = "User id", required = true)
            @RequestParam UUID userId,

            @ApiParam(value = "Chat name", required = true)
            @RequestParam String name,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT create chat request from %s, name: %s, userId: %s ", header.getHost(), name, userId));
        return toApi(chatService.create(name, userId));
    }

    @ApiOperation(value = "Get chat users", response = List.class)
    @GetMapping("/{chatId}/users")
    public List<UserApiModel> getUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat users request from %s, chatId:%s ", header.getHost(), chatId));
        return UserDataConverter.toApi(chatService.getChatUsers(chatId));
    }

    @ApiOperation(value = "Put chat users", response = ChatApiModel.class)
    @PutMapping("/{chatId}/users")
    public ChatApiModel addUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestParam List<UUID> userUUIDs,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT chat users request from %s, chatId:%s,  userUUIDs: {%s}",
                header.getHost(), chatId, userUUIDs.stream().map(UUID::toString).collect(Collectors.joining())));
        return toApi(chatService.addUsers(chatId, userUUIDs));
    }

    @ApiOperation(value = "Get chat messages", response = List.class)
    @GetMapping("/{chatId}/messages")
    public List<MessageApiModel> getMessages(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat messages request from %s, chatId:%s ", header.getHost(), chatId));
        return MessagesDataConverter.toApi(chatService.getChatMessages(chatId));
    }
}
