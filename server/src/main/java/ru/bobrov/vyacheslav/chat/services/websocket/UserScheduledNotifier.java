package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@RequiredArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Slf4j
public class UserScheduledNotifier {
    @NonNull
    final UserNotifyService userNotifyService;
    @NonNull
    final ScheduledExecutorService scheduledExecutorService;
    final Map<UUID, ScheduledFuture<?>> userNotifiers = new HashMap<>();

    @Value("${jwt.jwtTokenValidity}")
    long jwtTokenValidity;

    public void tokenExpired(UUID userId) {
        val jwtTokenValidityMs = TimeUnit.MINUTES.toMillis(jwtTokenValidity);

        val oldFuture = userNotifiers.get(userId);
        if (Objects.nonNull(oldFuture))
            oldFuture.cancel(false);

        val future = scheduledExecutorService.schedule(() -> {
            userNotifyService.tokenExpired(userId);
            log.info(format("Token for userId %s expired", userId));
        }, jwtTokenValidityMs - jwtTokenValidityMs * 10 / 100, TimeUnit.MILLISECONDS);
        userNotifiers.put(userId, future);
    }
}
