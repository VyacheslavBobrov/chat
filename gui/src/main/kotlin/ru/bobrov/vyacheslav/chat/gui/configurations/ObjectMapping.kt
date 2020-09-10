package ru.bobrov.vyacheslav.chat.gui.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapping {
    @Bean
    fun mapper(): ObjectMapper = ObjectMapper()
}