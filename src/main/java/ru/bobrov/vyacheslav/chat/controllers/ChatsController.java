package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.controllers.models.ChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.MessagesPagingApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.UserApiModel;

import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Utils.toDo;

@Api("Chats management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/chat")
@Slf4j
public class ChatsController {

    @ApiOperation(value = "Get chat by uuid", response = ChatApiModel.class)
    @GetMapping("/{chatId}")
    public ChatApiModel get(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toDo();
    }

    @ApiOperation(value = "Update chat data", response = ChatApiModel.class)
    @PutMapping("/{chatId}")
    public ChatApiModel update(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @ApiParam(value = "Chat name", required = true)
            @RequestParam(name = "name") String name,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT update chat request from %s, chatId:%s, name: %s ", header.getHost(), chatId, name));
        return toDo();
    }

    @ApiOperation(value = "Block chat", response = ChatApiModel.class)
    @GetMapping("/{chatId}/block")
    public ChatApiModel block(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET block chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toDo();
    }

    @ApiOperation(value = "Unlock chat", response = ChatApiModel.class)
    @GetMapping("/{chatId}/unblock")
    public ChatApiModel unblock(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET unblock chat request from %s, chatId:%s ", header.getHost(), chatId));
        return toDo();
    }

    @ApiOperation(value = "Create chat", response = ChatApiModel.class)
    @PutMapping
    public ChatApiModel create(
            @ApiParam(value = "User id", required = true)
            @RequestParam(value = "userId") UUID userId,

            @ApiParam(value = "Chat name", required = true)
            @RequestParam(name = "name") String name,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT create chat request from %s, name: %s, userId: %s ", header.getHost(), name, userId));
        return toDo();
    }

    @ApiOperation(value = "Get chat users", response = List.class)
    @GetMapping("/{chatId}/users")
    public List<UserApiModel> getUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat users request from %s, chatId:%s ", header.getHost(), chatId));
        return toDo();
    }

    @ApiOperation(value = "Get chat messages", response = List.class)
    @GetMapping("/{chatId}/messages")
    public MessagesPagingApiModel getMessages(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable(name = "chatId") UUID chatId,

            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat messages request from %s, chatId:%s ", header.getHost(), chatId));
        return toDo();
    }
}
