package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.bobrov.vyacheslav.chat.services.utils.JwtAuthenticationService;

import java.security.Principal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.bobrov.vyacheslav.chat.services.Constants.AUTHORIZATION_HEADER;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NonNull
@Component
@Slf4j
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    JwtAuthenticationService authenticationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (isNull(accessor)) {
            log.info("Accessor is null");
            return null;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authToken = accessor.getFirstNativeHeader(AUTHORIZATION_HEADER);
            log.info("Header auth token: " + authToken);
            if (isNull(authToken)) {
                log.info("Token is null");
                return null;
            }
            authenticationService.authenticate(authToken);
            Principal principal = SecurityContextHolder.getContext().getAuthentication();

            if (isNull(principal))
                return null;

            accessor.setUser(principal);
        } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (nonNull(authentication))
                log.info("Disconnected Auth : " + authentication.getName());
            else
                log.info("Disconnected Sess : " + accessor.getSessionId());
        }
        return message;
    }
}
