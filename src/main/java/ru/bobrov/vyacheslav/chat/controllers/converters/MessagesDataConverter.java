package ru.bobrov.vyacheslav.chat.controllers.converters;

import ru.bobrov.vyacheslav.chat.controllers.models.MessageApiModel;
import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesDataConverter {
    public static MessageApiModel toApi(Message message) {
        return MessageApiModel.builder()
                .messageId(message.getMessageId())
                .chat(ChatDataConverter.toApi(message.getChat()))
                .user(UserDataConverter.toApi(message.getUser()))
                .message(message.getMessage())
                .status(message.getStatus().name())
                .created(message.getCreated().toString())
                .updated(message.getUpdated().toString())
                .build();
    }

    public static List<MessageApiModel> toApi(Collection<Message> messages) {
        return messages.stream().map(MessagesDataConverter::toApi).collect(Collectors.toUnmodifiableList());
    }
}
