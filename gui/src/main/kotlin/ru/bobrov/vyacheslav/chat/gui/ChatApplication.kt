package ru.bobrov.vyacheslav.chat.gui

import javafx.application.Application
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ChatApplication

fun main(args: Array<String>) {
    Application.launch(FxApplication::class.java, *args)
}