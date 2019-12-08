package ru.bobrov.vyacheslav.chat.services.websocket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
    Map<String, Timer> userTimers = new HashMap<>();

    @Value("${jwt.jwtTokenValidity}")
    long jwtTokenValidity;

    public void tokenExpired(String user) {
        final Timer timer = userTimers.get(user);
        if (timer != null)
            timer.cancel();
        final Timer newTimer = new Timer(user, true);
        newTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                userNotifyService.tokenExpired(user);
                log.info(format("Token for user %s expired", user));
            }
        }, TimeUnit.MINUTES.toMillis(jwtTokenValidity - Double.valueOf(jwtTokenValidity * 0.1).longValue()));
        userTimers.put(user, newTimer);
    }
}
