package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
    @NonNull UserNotifyService userNotifyService;
    @NonNull ScheduledExecutorService scheduledExecutorService;
    Map<String, ScheduledFuture<?>> userNotifiers = new HashMap<>();

    @Value("${jwt.jwtTokenValidity}")
    long jwtTokenValidity;

    public void tokenExpired(String user) {
        final long jwtTokenValidityMs = TimeUnit.MINUTES.toMillis(jwtTokenValidity);

        ScheduledFuture<?> oldFuture = userNotifiers.get(user);
        if (Objects.nonNull(oldFuture))
            oldFuture.cancel(false);

        ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> {
            userNotifyService.tokenExpired(user);
            log.info(format("Token for user %s expired", user));
        }, jwtTokenValidityMs - jwtTokenValidityMs * 10 / 100, TimeUnit.MILLISECONDS);
        userNotifiers.put(user, future);
    }
}
