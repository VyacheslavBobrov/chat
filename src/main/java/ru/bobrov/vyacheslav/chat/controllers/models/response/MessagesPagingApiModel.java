package ru.bobrov.vyacheslav.chat.controllers.models.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
@NonNull
@ApiModel
public class MessagesPagingApiModel {
    List<MessageApiModel> messages;
    Integer page;
    Integer pageLimit;
    Long totalItems;
}
