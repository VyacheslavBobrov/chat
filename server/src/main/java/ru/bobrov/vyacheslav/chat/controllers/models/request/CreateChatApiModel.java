package ru.bobrov.vyacheslav.chat.controllers.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
public class CreateChatApiModel {
    UUID userId;
    String title;
}
