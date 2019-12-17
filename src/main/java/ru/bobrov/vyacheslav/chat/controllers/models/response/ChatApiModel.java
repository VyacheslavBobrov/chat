package ru.bobrov.vyacheslav.chat.controllers.models.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
@ApiModel
public class ChatApiModel {
    UUID chatId;
    String title;
    @ApiModelProperty (allowableValues = "ACTIVE, DISABLED")
    String status;
    String created;
    String updated;
    UserApiModel creator;
}
