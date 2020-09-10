package ru.bobrov.vyacheslav.chat.gui.dataproviders

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.bobrov.vyacheslav.chat.dto.response.UserRegistrationApiModel
import ru.bobrov.vyacheslav.chat.gui.dto.User
import ru.bobrov.vyacheslav.chat.gui.dto.toDto
import java.net.URI

private const val AUTHENTICATE_PATH = "/api/v1/authentication"

@Service
class AuthenticationService(
        @Value("\${chatserver.host}")
        private val host: String,
        private val mapper: ObjectMapper
) {
    private val loggedUser = ThreadLocal<User>()

    fun authenticate(login: String, password: String): User = mapper.readValue(
            send(
                    method = Methods.POST,
                    uri = URI.create("$host$AUTHENTICATE_PATH"),
                    params = listOf(
                            "login" to login,
                            "password" to password
                    )
            ),
            UserRegistrationApiModel::class.java
    )
            .toDto()
            .apply {
                loggedUser.set(this)
            }

    val user: User
        get() = loggedUser.get()
}
