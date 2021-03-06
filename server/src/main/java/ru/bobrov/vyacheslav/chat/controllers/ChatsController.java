package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bobrov.vyacheslav.chat.controllers.converters.MessagesDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dto.response.ChatApiModel;
import ru.bobrov.vyacheslav.chat.dto.response.MessagesPagingApiModel;
import ru.bobrov.vyacheslav.chat.dto.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.services.ChatService;
import ru.bobrov.vyacheslav.chat.services.MessageService;
import ru.bobrov.vyacheslav.chat.services.UserService;
import ru.bobrov.vyacheslav.chat.services.websocket.ChatListNotifyService;
import ru.bobrov.vyacheslav.chat.services.websocket.ChatNotifyService;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.ChatDataConverter.toApi;

@Api("Chats management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/chat")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin
@NonNull
public class ChatsController {
    ChatService chatService;
    UserService userService;
    MessageService messageService;
    ChatListNotifyService chatListNotifyService;
    ChatNotifyService chatNotifyService;

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

    @PreAuthorize("@chatSecurityPolicy.canUpdateChat(principal, #chatId)")
    @ApiOperation(value = "Update chat data", response = ChatApiModel.class)
    @PostMapping("/{chatId}")
    public ChatApiModel update(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestParam String title,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST update chat request from %s, chatId:%s, name: %s ",
                header.getHost(), chatId, title));

        val renamedChat = toApi(chatService.update(chatId, title));
        chatListNotifyService.nameChanged(chatId);
        chatNotifyService.nameChanged(chatId);
        return renamedChat;
    }

    @PreAuthorize("@chatSecurityPolicy.canBlockOrUnblockChat(principal, #chatId)")
    @ApiOperation(value = "Block chat", response = ChatApiModel.class)
    @PostMapping("/{chatId}/block")
    public ChatApiModel block(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT block chat request from %s, chatId:%s ", header.getHost(), chatId));
        val blockedChat = toApi(chatService.block(chatId));
        chatListNotifyService.chatBlocked(chatId);
        chatNotifyService.blocked(chatId);
        return blockedChat;
    }

    @PreAuthorize("@chatSecurityPolicy.canBlockOrUnblockChat(principal, #chatId)")
    @ApiOperation(value = "Unlock chat", response = ChatApiModel.class)
    @PostMapping("/{chatId}/unblock")
    public ChatApiModel unblock(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT unblock chat request from %s, chatId:%s ", header.getHost(), chatId));
        val unblockedChat = toApi(chatService.unblock(chatId));
        chatListNotifyService.chatUnblocked(chatId);
        chatNotifyService.unblocked(chatId);
        return unblockedChat;
    }

    @ApiOperation(value = "Create chat", response = ChatApiModel.class)
    @PostMapping
    public ChatApiModel create(
            @RequestParam UUID userId,
            @RequestParam String title,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST create chat request from %s, title: %s, userId: %s ",
                header.getHost(), title, userId));
        val chat = chatService.create(title, userId);
        return toApi(chat);
    }

    @PreAuthorize("@chatSecurityPolicy.canReadChat(principal, #chatId)")
    @ApiOperation(value = "Get chat users", response = UserApiModel.class, responseContainer = "List")
    @GetMapping("/{chatId}/users")
    public List<UserApiModel> getUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat users request from %s, chatId:%s ", header.getHost(), chatId));
        return UserDataConverter.toApi(chatService.getChatUsers(chatId));
    }

    @PreAuthorize("@chatSecurityPolicy.canReadChat(principal, #chatId)")
    @ApiOperation(value = "Get users out of chat", response = UserApiModel.class, responseContainer = "List")
    @GetMapping("/{chatId}/users-out")
    public List<UserApiModel> getUsersOutChat(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET chat users out of chat request from %s, chatId:%s ", header.getHost(), chatId));
        return UserDataConverter.toApi(userService.getAllActiveUsersOutOfChat(chatId));
    }

    @PreAuthorize("@chatSecurityPolicy.canUpdateChat(principal, #chatId)")
    @ApiOperation(value = "Put chat users", response = UserApiModel.class, responseContainer = "List")
    @PostMapping("/{chatId}/users")
    public List<UserApiModel> addUsers(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestParam List<UUID> userUUIDs,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST chat users request from %s, chatId:%s,  userUUIDs: {%s}",
                header.getHost(), chatId, userUUIDs.stream().map(UUID::toString).collect(Collectors.joining())));
        val updatedChatUsersList = UserDataConverter.toApi(chatService.addUsers(chatId, userUUIDs));
        chatListNotifyService.newChatUser(chatId);
        chatNotifyService.addUser(chatId);
        return updatedChatUsersList;
    }

    @PreAuthorize("@chatSecurityPolicy.canKickUser(principal, #chatId, #userId)")
    @ApiOperation(value = "Kick user from chat", response = UserApiModel.class, responseContainer = "List")
    @PostMapping("/{chatId}/kick")
    public List<UserApiModel> kickUser(
            @ApiParam(value = "Chat uuid", required = true)
            @PathVariable UUID chatId,
            @RequestParam UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST kick user chat user request from %s, chatId:%s,  userId: {%s}",
                header.getHost(), chatId, userId));
        val updatedChatUsersList = UserDataConverter.toApi(chatService.kickUser(chatId, userId));
        chatNotifyService.kickUser(chatId);
        return updatedChatUsersList;
    }

    @PreAuthorize("@chatSecurityPolicy.canReadChat(principal, #chatId)")
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

        val messagePage = messageService.getChatMessages(chatId, page, size);

        return MessagesPagingApiModel.builder()
                .messages(
                        messagePage.getContent().stream()
                                .sorted(Comparator.comparing(Message::getCreated))
                                .map(MessagesDataConverter::toApi)
                                .collect(Collectors.toUnmodifiableList())
                )
                .page(page)
                .pageLimit(messagePage.getTotalPages())
                .totalItems(messagePage.getTotalElements())
                .build();
    }
}
