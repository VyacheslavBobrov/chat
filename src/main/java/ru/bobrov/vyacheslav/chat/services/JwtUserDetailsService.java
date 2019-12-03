package ru.bobrov.vyacheslav.chat.services;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.UserStatus;
import ru.bobrov.vyacheslav.chat.services.dto.ChatUser;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
public class JwtUserDetailsService implements UserDetailsService {
    @NonNull UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByLogin(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found with username: " + username);

        return ChatUser.withId(user.getUserId())
                .userName(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(user.getStatus() == UserStatus.DISABLED)
                .build();
    }
}
