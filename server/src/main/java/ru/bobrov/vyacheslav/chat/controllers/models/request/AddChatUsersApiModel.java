package ru.bobrov.vyacheslav.chat.controllers.models.request;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
@Builder
@NonNull
public class AddChatUsersApiModel {
    List<UUID> userUUIDs;
}
