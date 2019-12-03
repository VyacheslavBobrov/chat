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
public class UploadFileApiModel {
    @ApiModelProperty
    UUID fileUUID;
    @ApiModelProperty
    String fileDownloadUri;
    @ApiModelProperty
    String fileType;
    @ApiModelProperty
    long size;
}
