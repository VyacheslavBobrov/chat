package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.configs.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.controllers.models.response.JwtResponse;
import ru.bobrov.vyacheslav.chat.controllers.models.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.services.JwtUserDetailsService;
import ru.bobrov.vyacheslav.chat.services.UserService;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.controllers.converters.UserDataConverter.toApi;

@Api("Chat authentication system")
@RestController
@CrossOrigin
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/authentication")
@FieldDefaults(level = PRIVATE)
@Slf4j
@NonNull
public class JwtAuthenticationController {
    AuthenticationManager authenticationManager;
    JwtTokenUtil jwtTokenUtil;
    JwtUserDetailsService jwtUserDetailsService;
    UserService userService;

    @ApiOperation(value = "Authenticate user", response = JwtResponse.class)
    @PostMapping
    public JwtResponse createAuthenticationToken(
            @RequestParam String login,
            @RequestParam String password
    ) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(login);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return JwtResponse.builder().jwtToken(token).build();
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
