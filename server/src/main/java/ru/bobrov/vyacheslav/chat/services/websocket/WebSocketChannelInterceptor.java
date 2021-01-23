package ru.bobrov.vyacheslav.chat.services.websocket;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.services.authentication.JwtAuthenticationService;

import java.security.Principal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@NonNull
@Component
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    JwtAuthenticationService authenticationService;

    @Override
    public Message<?> preSend(@org.springframework.lang.NonNull Message<?> message,
                              @org.springframework.lang.NonNull MessageChannel channel) {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (isNull(accessor)) {
            log.info("Accessor is null");
            return null;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            val authHeader = accessor.getFirstNativeHeader(AUTHORIZATION);
            log.info("Header auth token: " + authHeader);
            if (isNull(authHeader)) {
                log.info("Token is null");
                return null;
            }
            val principal = extractPrincipalFromHeader(authHeader);

            if (isNull(principal))
                return null;

            accessor.setUser(principal);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            val user = accessor.getUser();

            if (nonNull(user))
                log.info("Disconnected Auth : " + user.getName());
            else
                log.info("Disconnected Sess : " + accessor.getSessionId());
        }
        return message;
    }

    private Principal extractPrincipalFromHeader(String header) {
        try {
            return authenticationService.createPrincipalFromToken(authenticationService.extractTokenFromHeader(header));
        } catch (IllegalArgumentException e) {
            log.error("Unable to create principal from token", e);
        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            log.error("JWT Token has expired", e);
        }

        return null;
    }
}
