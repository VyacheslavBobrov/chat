package ru.bobrov.vyacheslav.chat.services.utils;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Component
@AllArgsConstructor
@Service
@FieldDefaults(level = PRIVATE)
public class Translator {
    ResourceBundleMessageSource messageSource;

    public String translate(String messageCode) {
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }
}
