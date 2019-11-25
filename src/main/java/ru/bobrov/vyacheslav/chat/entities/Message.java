package ru.bobrov.vyacheslav.chat.entities;

import lombok.Data;

import java.util.UUID;

@Data
public class Message {
    private UUID id;
    private Chat chat;
    private User user;
    private String text;
    private MessageStatus status;
}
