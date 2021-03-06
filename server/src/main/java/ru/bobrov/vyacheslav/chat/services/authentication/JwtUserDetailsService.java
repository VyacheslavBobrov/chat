package ru.bobrov.vyacheslav.chat.services.authentication;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dto.enums.UserStatus;
import ru.bobrov.vyacheslav.chat.services.UserService;
import ru.bobrov.vyacheslav.chat.services.dto.ChatUser;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Service
@AllArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtUserDetailsService implements UserDetailsService {
    @NonNull UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = userService.getUserByLogin(username);
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
