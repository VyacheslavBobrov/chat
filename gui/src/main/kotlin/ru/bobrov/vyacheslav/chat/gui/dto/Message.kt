package ru.bobrov.vyacheslav.chat.gui.dto

import ru.bobrov.vyacheslav.chat.dto.enums.MessageStatus
import java.util.*

data class Message(
        val messageId: UUID,
        val chat: Chat,
        val user: User,
        val message: String,
        val status: MessageStatus,
        val created: Long,
        val updated: Long
)