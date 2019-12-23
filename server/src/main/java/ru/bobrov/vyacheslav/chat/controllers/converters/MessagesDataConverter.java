package ru.bobrov.vyacheslav.chat.controllers.converters;

import ru.bobrov.vyacheslav.chat.dataproviders.entities.Message;
import ru.bobrov.vyacheslav.chat.dto.response.MessageApiModel;

import java.util.Collection;
import java.util.Comparator;
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
                .created(message.getCreated().getTime())
                .updated(message.getUpdated().getTime())
                .build();
    }

    public static List<MessageApiModel> toApi(Collection<Message> messages) {
        return messages.stream()
                //внутри страницы нужно поменять порядок сообщений, как это принято в чатах (внизу самые свежие)
                .sorted(Comparator.comparing(Message::getCreated).reversed())
                .map(MessagesDataConverter::toApi)
                .collect(Collectors.toUnmodifiableList());
    }
}
