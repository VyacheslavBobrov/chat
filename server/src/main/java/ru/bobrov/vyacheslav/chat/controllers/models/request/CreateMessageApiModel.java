package ru.bobrov.vyacheslav.chat.controllers.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
public class CreateMessageApiModel {
    UUID chatId;
    UUID userId;
    String message;
}
