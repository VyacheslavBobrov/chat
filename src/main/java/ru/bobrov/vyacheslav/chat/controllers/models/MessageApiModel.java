package ru.bobrov.vyacheslav.chat.controllers.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
public class MessageApiModel {
    UUID messageId;
    ChatApiModel chat;
    UserApiModel user;
    String message;
    String status;
    String created;
    String updated;
}
