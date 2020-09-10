package ru.bobrov.vyacheslav.chat.gui.dataproviders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bobrov.vyacheslav.chat.dto.response.ChatApiModel
import ru.bobrov.vyacheslav.chat.dto.response.MessagesPagingApiModel
import ru.bobrov.vyacheslav.chat.dto.response.UserApiModel
import ru.bobrov.vyacheslav.chat.gui.dto.Chat
import ru.bobrov.vyacheslav.chat.gui.dto.MessagePage
import ru.bobrov.vyacheslav.chat.gui.dto.User
import ru.bobrov.vyacheslav.chat.gui.dto.toDto
import java.lang.String.format
import java.net.URI
import java.util.*

private const val CHATS_PATH = "/api/v1/chat"

private const val CHAT_USERS_PATH = "%s/users"
private const val GET_USERS_OUT_PATH = "%s/users-out"
private const val KICK_USER_PATH = "%s/kick"

private const val GET_MESSAGES_PATH = "%s/messages"

private const val BLOCK_CHAT_PATH = "%s/block"
private const val UNBLOCK_CHAT_PATH = "%s/unblock"

@Service
class ChatsService(
        @Value("\${chatserver.host}")
        private val host: String,
        private val authenticationService: AuthenticationService,
        private val netService: NetService,
        private val mapper: ObjectMapper
) {
    private val chatsPath = "$host$CHATS_PATH"

    fun get(chatId: UUID): Chat =
            mapper.readValue(
                    netService.get(URI.create("$chatsPath/$chatId")),
                    ChatApiModel::class.java
            ).toDto()

    fun update(chatId: UUID, title: String): Chat {
        if (title.isBlank())
            throw IllegalArgumentException()

        return mapper.readValue(
                netService.post(URI.create("$chatsPath/$chatId")),
                ChatApiModel::class.java
        ).toDto()
    }

    fun create(title: String): Chat {
        if (title.isBlank())
            throw IllegalArgumentException()

        return mapper.readValue(
                netService.post(
                        URI.create(chatsPath),
                        listOf(
                                "userId" to authenticationService.user.userId,
                                "title" to title
                        )
                ),
                ChatApiModel::class.java
        ).toDto()
    }

    fun getUsers(chatId: UUID): List<User> =
            mapper.readValue<List<UserApiModel>>(
                    netService.get(URI.create(format("$chatsPath/$CHAT_USERS_PATH", chatId))),
                    mapper.typeFactory.constructCollectionType(List::class.java, UserApiModel::class.java)
            ).map { it.toDto() }

    fun getUsersOutChat(chatId: UUID): List<User> =
            mapper.readValue<List<UserApiModel>>(
                    netService.get(URI.create(format("$chatsPath/$GET_USERS_OUT_PATH", chatId))),
                    mapper.typeFactory.constructCollectionType(List::class.java, UserApiModel::class.java)
            ).map { it.toDto() }

    fun addUsers(chatId: UUID, userIds: List<UUID>): List<User> =
            mapper.readValue<List<UserApiModel>>(
                    netService.post(
                            URI.create(format("$chatsPath/$CHAT_USERS_PATH", chatId)),
                            listOf("userUUIDs" to userIds.joinToString(prefix = "[", postfix = "]"))
                    ),
                    mapper.typeFactory.constructCollectionType(List::class.java, UserApiModel::class.java)
            ).map { it.toDto() }

    fun kickUser(chatId: UUID, userId: UUID): List<User> =
            mapper.readValue<List<UserApiModel>>(
                    netService.post(
                            URI.create(format("$chatsPath/$KICK_USER_PATH", chatId)),
                            listOf("userId" to userId)
                    ),
                    mapper.typeFactory.constructCollectionType(List::class.java, UserApiModel::class.java)
            ).map { it.toDto() }

    fun getMessages(chatId: UUID, page: Int, size: Int): MessagePage =
            mapper.readValue(
                    netService.get(
                            URI.create(format("$chatsPath/$GET_MESSAGES_PATH", chatId)),
                            listOf(
                                    "page" to page,
                                    "size" to size
                            )
                    ),
                    MessagesPagingApiModel::class.java
            ).toDto()

    fun block(chatId: UUID): Chat =
            mapper.readValue(
                    netService.post(URI.create(format("$chatsPath/$BLOCK_CHAT_PATH", chatId))),
                    ChatApiModel::class.java
            ).toDto()

    fun unblock(chatId: UUID): Chat =
            mapper.readValue(
                    netService.post(URI.create(format("$chatsPath/$UNBLOCK_CHAT_PATH", chatId))),
                    ChatApiModel::class.java
            ).toDto()
}