package ru.bobrov.vyacheslav.chat.dto.response;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@NonNull
@ApiModel
public class MessagesPagingApiModel {
    List<MessageApiModel> messages;
    Integer page;
    Integer pageLimit;
    Long totalItems;
}
