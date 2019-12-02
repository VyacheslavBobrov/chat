package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.controllers.converters.ChatDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter;
import ru.bobrov.vyacheslav.chat.controllers.models.response.ChatApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.controllers.models.response.UsersPagingApiModel;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.services.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter.toApi;

@Api("Chat users management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/user")
@FieldDefaults(level = PRIVATE)
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
public class UsersController {
    @NonNull UserService userService;

    @ApiOperation(value = "Get user by uuid", response = UserApiModel.class)
    @GetMapping("/{userId}")
    public UserApiModel get(
            @ApiParam(value = "User uuid", required = true)
            @PathVariable UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET user request from %s, userId:%s ", header.getHost(), userId));
        return toApi(userService.get(userId));
    }

    @ApiOperation(value = "Update user data", response = UserApiModel.class)
    @PostMapping("/{userId}")
    public UserApiModel update(
            @ApiParam(value = "User uuid", required = true)
            @PathVariable UUID userId,
            @RequestParam String name,
            @RequestParam String login,
            @RequestParam String password,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST request for update user, from %s, userId: %s, name: %s, login: %s",
                header.getHost(), userId, name, login));
        return toApi(userService.update(userId, name, login, password));
    }

    @ApiOperation(value = "Block user by uuid", response = UserApiModel.class)
    @PutMapping("/{userId}/block")
    public UserApiModel block(
            @ApiParam(value = "User uuid", required = true)
            @PathVariable UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT request to block user, from %s, userId: %s", header.getHost(), userId));
        return toApi(userService.block(userId));
    }

    @ApiOperation(value = "Unblock user by uuid", response = UserApiModel.class)
    @PutMapping("/{userId}/unblock")
    public UserApiModel unblock(
            @ApiParam(value = "User uuid", required = true)
            @PathVariable UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("PUT request to unblock user, from %s, userId: %s", header.getHost(), userId));
        return toApi(userService.unblock(userId));
    }

    @ApiOperation(value = "Get all users", response = UsersPagingApiModel.class)
    @GetMapping
    public UsersPagingApiModel get(
            @ApiParam(value = "Users list page number", required = true)
            @RequestParam Integer page,
            @ApiParam(value = "Users list page size", required = true)
            @RequestParam Integer size,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET request to get all users, from: %s, page: %d, size: %d",
                header.getHost(), page, size));

        Page<User> userPage = userService.getAllActiveUsers(page, size);

        return UsersPagingApiModel.builder()
                .ids(userPage.get().map(User::getUserId).collect(Collectors.toUnmodifiableList()))
                .items(userPage.get().collect(Collectors.toUnmodifiableMap(User::getUserId, UserDataConverter::toApi)))
                .page(page)
                .pageLimit(userPage.getTotalPages())
                .totalItems(userPage.getTotalElements())
                .build();
    }

    @ApiOperation(value = "Get chats for user", response = ChatApiModel.class, responseContainer = "List")
    @GetMapping("/{userId}/chats")
    public List<ChatApiModel> getChats(
            @ApiParam(value = "User uuid", required = true)
            @PathVariable(name = "userId") UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET user chats request from %s, userId:%s ", header.getHost(), userId));
        return ChatDataConverter.toApi(userService.getUserChats(userId));
    }
}
