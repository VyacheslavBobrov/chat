package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@NonNull
@ApiModel
public class UserApiModel {
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
}
