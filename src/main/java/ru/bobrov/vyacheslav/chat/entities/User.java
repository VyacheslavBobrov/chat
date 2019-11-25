package ru.bobrov.vyacheslav.chat.entities;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class User {
    private UUID id;
    private String name;
    private String login;
    private String password;
    private UserStatus status;
    private UserRole role;
    private List<Chat> chats;
}
