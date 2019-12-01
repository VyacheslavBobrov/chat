package ru.bobrov.vyacheslav.chat.controllers.models.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UserApiModel {
    @NonNull UUID userId;
    @NonNull String name;
    @NonNull String login;
    @NonNull String status;
    @NonNull String role;
    @NonNull String created;
    @NonNull String updated;
}
