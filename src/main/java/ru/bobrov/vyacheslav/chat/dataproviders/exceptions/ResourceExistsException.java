package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

public class ResourceExistsException extends RuntimeException {
    public ResourceExistsException(String message) {
        super(message);
    }
}
