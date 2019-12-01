package ru.bobrov.vyacheslav.chat.controllers.converters;

import ru.bobrov.vyacheslav.chat.controllers.models.response.ChatApiModel;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Chat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChatDataConverter {
    public static ChatApiModel toApi(Chat chat) {
        return ChatApiModel.builder()
                .chatId(chat.getChatId())
                .name(chat.getName())
                .status(chat.getStatus().name())
                .created(chat.getCreated().toString())
                .updated(chat.getUpdated().toString())
                .creator(UserDataConverter.toApi(chat.getCreator()))
                .build();
    }

    public static List<ChatApiModel> toApi(Collection<Chat> chats) {
        return chats.stream().map(ChatDataConverter::toApi).collect(Collectors.toUnmodifiableList());
    }
}
