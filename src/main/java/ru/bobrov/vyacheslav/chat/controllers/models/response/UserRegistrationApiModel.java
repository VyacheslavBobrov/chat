package ru.bobrov.vyacheslav.chat.controllers.models.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
@ApiModel
public class UserRegistrationApiModel {
    @ApiModelProperty
    UUID userId;
    @ApiModelProperty
    String name;
    @ApiModelProperty
    String login;
    @ApiModelProperty(allowableValues = "ACTIVE, DISABLED")
    String status;
    @ApiModelProperty(allowableValues = "ADMIN, USER, GUEST")
    String role;
    @ApiModelProperty
    String created;
    @ApiModelProperty
    String updated;
    @ApiParam
    String jwtToken;
}
