package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@NonNull
@ApiModel
public class UploadFileApiModel {
    UUID fileUUID;
    String fileDownloadUri;
    String fileType;
    long size;
}
