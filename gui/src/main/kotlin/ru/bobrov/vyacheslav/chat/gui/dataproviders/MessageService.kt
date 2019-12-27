package ru.bobrov.vyacheslav.chat.gui.dataproviders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bobrov.vyacheslav.chat.dto.response.MessageApiModel
import ru.bobrov.vyacheslav.chat.gui.dto.Message
import ru.bobrov.vyacheslav.chat.gui.dto.toDto
import java.lang.String.format
import java.net.URI
import java.util.*

private const val MESSAGES_PATH = "/api/v1/message"

private const val BLOCK_MESSAGE_PATH = "/%s/block"
private const val UNBLOCK_MESSAGE_PATH = "/%s/unblock"

@Service
class MessageService(
        @Value("\${chatserver.host}")
        private val host: String,
        private val authenticationService: AuthenticationService,
        private val netService: NetService
) {
    private val mapper = ObjectMapper()
    private val messagesPath = "$host$MESSAGES_PATH"

    fun get(messageId: UUID): Message =
            mapper.readValue(
                    netService.get(URI.create("$messagesPath/$messageId")),
                    MessageApiModel::class.java
            ).toDto()

    fun update(messageId: UUID, message: String): Message {
        if (message.isBlank())
            throw IllegalArgumentException()

        return mapper.readValue(
                netService.post(
                        URI.create("$messagesPath/$messageId"),
                        listOf("message" to message)
                ),
                MessageApiModel::class.java
        ).toDto()
    }

    fun block(messageId: UUID): Message =
            mapper.readValue(
                    netService.post(URI.create(format("$messagesPath/$BLOCK_MESSAGE_PATH", messageId))),
                    MessageApiModel::class.java
            ).toDto()

    fun unblock(messageId: UUID): Message =
            mapper.readValue(
                    netService.post(URI.create(format("$messagesPath/$UNBLOCK_MESSAGE_PATH", messageId))),
                    MessageApiModel::class.java
            ).toDto()

    fun create(chatId: UUID, message: String): Message =
            mapper.readValue(
                    netService.post(
                            URI.create(messagesPath),
                            listOf(
                                    "chatId" to chatId,
                                    "userId" to authenticationService.user.userId,
                                    "message" to message
                            )
                    ),
                    MessageApiModel::class.java
            ).toDto()
}