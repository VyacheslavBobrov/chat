package ru.bobrov.vyacheslav.chat.controllers.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@NonNull
public class UpdateChatApiModel {
    String title;
}