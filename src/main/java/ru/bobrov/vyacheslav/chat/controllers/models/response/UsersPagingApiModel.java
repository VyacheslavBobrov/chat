package ru.bobrov.vyacheslav.chat.controllers.models.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Value
@Builder
@NonNull
@ApiModel
public class UsersPagingApiModel {
    @ApiModelProperty
    List<UUID> ids;
    @ApiModelProperty
    Map<UUID, UserApiModel> items;
    @ApiModelProperty
    Integer page;
    @ApiModelProperty
    Integer pageLimit;
    @ApiModelProperty
    Long totalItems;
}
