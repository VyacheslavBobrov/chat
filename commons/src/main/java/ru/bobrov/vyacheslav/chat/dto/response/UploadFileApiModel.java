package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@NonNull
@ApiModel
public class UploadFileApiModel {
    UUID fileUUID;
    String fileDownloadUri;
    String fileType;
    long size;
}
