package ru.bobrov.vyacheslav.chat.controllers.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
@NonNull
public class UsersPagingApiModel {
    List<UUID> ids;
    Map<UUID, UserApiModel> items;
    Integer page;
    Integer pageLimit;
    Long totalItems;
}
