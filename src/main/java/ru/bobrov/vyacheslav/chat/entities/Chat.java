package ru.bobrov.vyacheslav.chat.entities;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Chat {
    private UUID id;
    private String name;
    private ChatStatus status;
    private User creator;
    private List<User> users;
    private List<Message> messages;
}
