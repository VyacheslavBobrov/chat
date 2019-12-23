package ru.bobrov.vyacheslav.chat.controllers.converters;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;
import ru.bobrov.vyacheslav.chat.dto.response.ChatApiModel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDataConverter {
    public static ChatApiModel toApi(Chat chat) {
        return ChatApiModel.builder()
                .chatId(chat.getChatId())
                .title(chat.getTitle())
                .status(chat.getStatus().name())
                .created(chat.getCreated().toLocalDateTime().toString())
                .updated(chat.getUpdated().toLocalDateTime().toString())
                .creator(UserDataConverter.toApi(chat.getCreator()))
                .build();
    }

    public static List<ChatApiModel> toApi(Collection<Chat> chats) {
        return chats.stream().map(ChatDataConverter::toApi).collect(Collectors.toUnmodifiableList());
    }
}
