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
public class UserApiModel {
    UUID userId;
    String name;
    String login;
    @ApiModelProperty(allowableValues = "ACTIVE, DISABLED")
    String status;
    @ApiModelProperty(allowableValues = "ADMIN, USER, GUEST")
    String role;
    String created;
    String updated;
}
