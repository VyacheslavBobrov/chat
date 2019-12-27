package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@NonNull
@ApiModel
public class UsersPagingApiModel {
    List<UUID> ids;
    Map<UUID, UserApiModel> items;
    Integer page;
    Integer pageLimit;
    Long totalItems;
}
