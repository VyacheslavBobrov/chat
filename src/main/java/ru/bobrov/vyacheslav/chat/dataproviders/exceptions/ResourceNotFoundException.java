package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
public abstract class ResourceNotFoundException extends RuntimeException implements ChatExceptions {
    String title;
    String message;
}
