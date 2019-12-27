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
public class ChatApiModel {
    UUID chatId;
    String title;
    @ApiModelProperty(allowableValues = "ACTIVE, DISABLED")
    String status;
    String created;
    String updated;
    UserApiModel creator;
}
