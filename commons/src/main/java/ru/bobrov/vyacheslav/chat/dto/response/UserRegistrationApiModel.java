package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
@NonNull
@ApiModel
public class UserRegistrationApiModel {
    UUID userId;
    UUID userPic;
    String name;
    String login;
    @ApiModelProperty(allowableValues = "ACTIVE, DISABLED")
    String status;
    @ApiModelProperty(allowableValues = "ADMIN, USER, GUEST")
    String role;
    String created;
    String updated;
    String jwtToken;
}
