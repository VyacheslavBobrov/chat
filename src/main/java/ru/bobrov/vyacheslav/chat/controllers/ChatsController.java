package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.controllers.converters.MessagesDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.models.request.AddChatUsersApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.request.CreateChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.request.UpdateChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.ChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.MessagesPagingApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
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
    @PostMapping("/{chatId}")
    public ChatApiModel update(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestBody UpdateChatApiModel request,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST update chat request from %s, chatId:%s, name: %s ",
                header.getHost(), chatId, request.getTitle()));
        return toApi(chatService.update(chatId, request.getTitle()));
    }

    @ApiOperation(value = "Block chat", response = ChatApiModel.class)
    @PutMapping("/{chatId}/block")
    public ChatApiModel block(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT block chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toApi(chatService.block(chatId));
    }

    @ApiOperation(value = "Unlock chat", response = ChatApiModel.class)
    @PutMapping("/{chatId}/unblock")
    public ChatApiModel unblock(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT unblock chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toApi(chatService.unblock(chatId));
    }

    @ApiOperation(value = "Create chat", response = ChatApiModel.class)
    @PostMapping
    public ChatApiModel create(
            @RequestBody CreateChatApiModel request,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST create chat request from %s, name: %s, userId: %s ",
                header.getHost(), request.getTitle(), request.getUserId()));
        return toApi(chatService.create(request.getTitle(), request.getUserId()));
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
    @PostMapping("/{chatId}/users")
    public List<UserApiModel> addUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestBody AddChatUsersApiModel request,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT chat users request from %s, chatId:%s,  userUUIDs: {%s}",
                header.getHost(), chatId, request.getUserUUIDs().stream().map(UUID::toString).collect(Collectors.joining())));
        return UserDataConverter.toApi(chatService.addUsers(chatId, request.getUserUUIDs()));
    }

    @ApiOperation(value = "Get chat messages", response = MessagesPagingApiModel.class)
    @GetMapping("/{chatId}/messages")
    public MessagesPagingApiModel getMessages(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat messages request from %s, chatId:%s ", header.getHost(), chatId));

        Page<Message> messagePage = chatService.getChatMessages(chatId, page, size);

        return MessagesPagingApiModel.builder()
                .ids(messagePage.get().map(Message::getMessageId).collect(Collectors.toUnmodifiableList()))
                .items(messagePage.get().collect(Collectors.toUnmodifiableMap(Message::getMessageId, MessagesDataConverter::toApi)))
                .page(page)
                .pageLimit(messagePage.getTotalPages())
                .totalItems(messagePage.getTotalElements())
                .build();
    }
}
