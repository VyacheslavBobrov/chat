package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

public class ChatNotFoundException extends ResourceNotFoundException {
    public ChatNotFoundException(String title, String message) {
        super(title, message);
    }
}
