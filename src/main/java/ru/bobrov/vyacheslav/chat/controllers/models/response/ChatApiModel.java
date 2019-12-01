package ru.bobrov.vyacheslav.chat.controllers.models.response;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
public class ChatApiModel {
    UUID chatId;
    String title;
    String status;
    String created;
    String updated;
    UserApiModel creator;
}
