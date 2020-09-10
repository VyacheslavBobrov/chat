package ru.bobrov.vyacheslav.chat.gui.dataproviders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bobrov.vyacheslav.chat.dto.response.ChatApiModel
import ru.bobrov.vyacheslav.chat.dto.response.UserApiModel
import ru.bobrov.vyacheslav.chat.gui.dto.Chat
import ru.bobrov.vyacheslav.chat.gui.dto.User
import ru.bobrov.vyacheslav.chat.gui.dto.toDto
import java.lang.String.format
import java.net.URI
import java.util.*

private const val USERS_PATH = "/api/v1/user"

private const val GET_CHATS_PATH = "%s/chats"

private const val BLOCK_USER_PATH = "%s/block"
private const val UNBLOCK_USER_PATH = "%s/unblock"

@Service
class UsersService(
        @Value("\${chatserver.host}")
        private val host: String,
        private val authenticationService: AuthenticationService,
        private val netService: NetService,
        private val mapper: ObjectMapper
) {
    private val usersPath = "$host$USERS_PATH"

    fun getChats(): List<Chat> =
            mapper.readValue<List<ChatApiModel>>(
                    netService.get(URI.create(format("$usersPath/$GET_CHATS_PATH", authenticationService.user.userId))),
                    mapper.typeFactory.constructCollectionType(List::class.java, ChatApiModel::class.java)
            ).map { it.toDto() }

    fun get(userId: UUID): User =
            mapper.readValue(
                    netService.get(URI.create("$usersPath/$userId")), UserApiModel::class.java
            ).toDto()

    fun block(userId: UUID): User =
            mapper.readValue(
                    netService.post(URI.create(format("$usersPath/$BLOCK_USER_PATH", userId))),
                    UserApiModel::class.java
            ).toDto()

    fun unblock(userId: UUID): User =
            mapper.readValue(
                    netService.post(URI.create(format("$usersPath/$UNBLOCK_USER_PATH", userId))),
                    UserApiModel::class.java
            ).toDto()

    fun update(
            name: String? = null,
            userPic: UUID? = null,
            login: String? = null,
            password: String? = null
    ): User {
        val params = listOf(
                "name" to name,
                "userPic" to userPic,
                "login" to login,
                "password" to password
        )
                .filter { it.second != null }
                .map { it.first to it.second!! }

        if (params.isEmpty())
            throw IllegalArgumentException()

        return mapper.readValue(
                netService.post(
                        URI.create("$usersPath/" + authenticationService.user.userId),
                        params
                ),
                UserApiModel::class.java
        ).toDto()
    }
}