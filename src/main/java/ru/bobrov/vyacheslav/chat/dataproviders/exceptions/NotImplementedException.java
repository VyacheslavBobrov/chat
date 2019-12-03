package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public class NotImplementedException extends RuntimeException implements ChatExceptions {
    String title;
    String message;
}
