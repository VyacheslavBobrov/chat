package ru.bobrov.vyacheslav.chat.controllers.converters;

import org.springframework.stereotype.Service;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.User;
import ru.bobrov.vyacheslav.chat.dto.response.UserApiModel;
import ru.bobrov.vyacheslav.chat.dto.response.UserRegistrationApiModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDataConverter {
    static public UserApiModel toApi(User user) {
        return UserApiModel.builder()
                .userId(user.getUserId())
                .userPic(user.getUserPic())
                .login(user.getLogin())
                .name(user.getName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .created(user.getCreated().toLocalDateTime().toString())
                .updated(user.getUpdated().toLocalDateTime().toString())
                .build();
    }

    static public UserRegistrationApiModel toApi(User user, String token) {
        return UserRegistrationApiModel.builder()
                .userId(user.getUserId())
                .userPic(user.getUserPic())
                .login(user.getLogin())
                .name(user.getName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .created(user.getCreated().toLocalDateTime().toString())
                .updated(user.getUpdated().toLocalDateTime().toString())
                .jwtToken(token)
                .build();
    }

    static public List<UserApiModel> toApi(Collection<User> users) {
        return users.stream().map(UserDataConverter::toApi).collect(Collectors.toUnmodifiableList());
    }
}
