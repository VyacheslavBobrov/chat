package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bobrov.vyacheslav.chat.dto.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.dto.response.UserRegistrationApiModel;
import ru.bobrov.vyacheslav.chat.services.UserService;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.utils.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.services.websocket.UserScheduledNotifier;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter.toApi;

@Api("Chat authentication system")
@RestController
@CrossOrigin
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/authentication")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@NonNull
public class JwtAuthenticationController {
    AuthenticationManager authenticationManager;
    JwtTokenUtil jwtTokenUtil;
    JwtUserDetailsService jwtUserDetailsService;
    UserService userService;
    UserScheduledNotifier userScheduledNotifier;

    @ApiOperation(value = "Authenticate user", response = UserRegistrationApiModel.class)
    @PostMapping
    public UserRegistrationApiModel createAuthenticationToken(
            @RequestParam String login,
            @RequestParam String password,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST authentication request, from: %s, for login: %s", header.getHost(), login));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        val userDetails = jwtUserDetailsService.loadUserByUsername(login);
        val token = jwtTokenUtil.generateToken(userDetails);
        val user = userService.getUserByLogin(login);
        userScheduledNotifier.tokenExpired(user.getUserId());
        return toApi(user, token);
    }

    @ApiOperation(value = "Create new chat user", response = UserApiModel.class)
    @PostMapping("/registration")
    public UserApiModel create(
            @RequestParam String name,
            @RequestParam String login,
            @RequestParam String password,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST request to create user, from: %s, name: %s, login: %s",
                header.getHost(), name, login));
        return toApi(userService.create(name, login, password));
    }
}
