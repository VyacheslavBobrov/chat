package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

public class FileNotFoundException extends ResourceNotFoundException {
    public FileNotFoundException(String title, String message) {
        super(title, message);
    }
}
