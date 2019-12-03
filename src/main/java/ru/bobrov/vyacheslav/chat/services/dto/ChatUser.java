package ru.bobrov.vyacheslav.chat.services.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE)
public class ChatUser implements UserDetails {
    @Getter
    UUID id;
    UserDetails user;

    public static Builder withId(UUID userUUID) {
        return new Builder(userUUID, User.builder());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @FieldDefaults(level = PRIVATE)
    @AllArgsConstructor(access = PRIVATE)
    public static class Builder {
        UUID userUUID;
        User.UserBuilder userBuilder;

        public Builder userName(String userName) {
            userBuilder.username(userName);
            return this;
        }

        public Builder password(String password) {
            userBuilder.password(password);
            return this;
        }

        public Builder roles(String... roles) {
            userBuilder.roles(roles);
            return this;
        }

        public Builder disabled(boolean disabled) {
            userBuilder.disabled(disabled);
            return this;
        }

        public ChatUser build() {
            return new ChatUser(userUUID, userBuilder.build());
        }
    }
}
