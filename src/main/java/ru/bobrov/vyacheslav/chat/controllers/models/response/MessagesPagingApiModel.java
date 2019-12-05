package ru.bobrov.vyacheslav.chat.controllers.models.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
@NonNull
@ApiModel
public class MessagesPagingApiModel {
    @ApiModelProperty
    List<MessageApiModel> messages;
    @ApiModelProperty
    Integer page;
    @ApiModelProperty
    Integer pageLimit;
    @ApiModelProperty
    Long totalItems;
}
