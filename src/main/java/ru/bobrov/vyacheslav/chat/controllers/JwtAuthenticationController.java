package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.bobrov.vyacheslav.chat.configs.JwtTokenUtil;
import ru.bobrov.vyacheslav.chat.controllers.models.JwtResponse;
import ru.bobrov.vyacheslav.chat.services.JwtUserDetailsService;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

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

    @ApiOperation(value = "Authenticate user", response = JwtResponse.class)
    @PostMapping
    public JwtResponse createAuthenticationToken(
            @RequestParam String name,
            @RequestParam String password
    ) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(name);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return JwtResponse.builder().jwtToken(token).build();
    }
}
