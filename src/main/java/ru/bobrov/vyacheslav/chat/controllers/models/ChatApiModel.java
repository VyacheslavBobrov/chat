package ru.bobrov.vyacheslav.chat.controllers.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
public class ChatApiModel {
    UUID chatId;
    String name;
    String status;
    String created;
    String updated;
    UserApiModel creator;
}
