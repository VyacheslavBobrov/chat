package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Date;

@Value
@Builder
public class ErrorDetails {
    @NonNull Date timestamp;
    String message;
    String details;
}
