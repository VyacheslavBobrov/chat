package ru.bobrov.vyacheslav.chat.gui.dto

import ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus
import java.util.*

data class Chat(
        val chatId: UUID,
        val title: String,
        val status: ChatStatus,
        val created: String,
        val updated: String,
        val creator: User
)