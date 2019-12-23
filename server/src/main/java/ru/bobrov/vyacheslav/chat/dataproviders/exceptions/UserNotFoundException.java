package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String title, String message) {
        super(title, message);
    }
}
