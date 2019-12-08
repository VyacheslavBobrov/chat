package ru.bobrov.vyacheslav.chat.configurations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ru.bobrov.vyacheslav.chat.services.websocket.HttpHandshakeInterceptor;
import ru.bobrov.vyacheslav.chat.services.websocket.WebSocketChannelInterceptor;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@NonNull
@Controller
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    HttpHandshakeInterceptor handshakeInterceptor;
    WebSocketChannelInterceptor channelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat", "/user");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/chat-messaging")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor);
        registry
                .addEndpoint("/chat-messaging")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor);
    }
}
