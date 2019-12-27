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
public class MessageApiModel {
    UUID messageId;
    ChatApiModel chat;
    UserApiModel user;
    String message;
    @ApiModelProperty(allowableValues = "ACTIVE, DISABLED")
    String status;
    Long created;
    Long updated;
}
