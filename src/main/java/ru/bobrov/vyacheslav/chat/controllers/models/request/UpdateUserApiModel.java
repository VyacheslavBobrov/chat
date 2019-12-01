package ru.bobrov.vyacheslav.chat.controllers.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class UpdateUserApiModel {
    String name;
    String login;
    String password;
}
