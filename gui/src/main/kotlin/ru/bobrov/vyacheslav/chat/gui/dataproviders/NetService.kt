package ru.bobrov.vyacheslav.chat.gui.dataproviders

import org.springframework.stereotype.Service
import ru.bobrov.vyacheslav.chat.gui.dataproviders.Methods.GET
import ru.bobrov.vyacheslav.chat.gui.dataproviders.Methods.POST
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

@Service
class NetService(
        private val authenticationService: AuthenticationService
) {
    fun post(uri: URI, params: List<Pair<String, Any>> = listOf(), headers: List<Pair<String, Any>> = listOf()): String {
        return send(
                method = POST,
                uri = uri,
                params = params,
                headers = headers + listOf("Authentication" to "Bearer ${authenticationService.user.jwtToken}")
        )
    }

    fun get(uri: URI, params: List<Pair<String, Any>> = listOf(), headers: List<Pair<String, Any>> = listOf()): String {
        return send(
                method = GET,
                uri = uri,
                params = params,
                headers = headers + listOf("Authentication" to "Bearer ${authenticationService.user.jwtToken}")
        )
    }
}

enum class Methods {
    POST,
    GET
}

fun send(
        method: Methods,
        uri: URI,
        params: List<Pair<String, Any>> = listOf(),
        headers: List<Pair<String, Any>> = listOf()
): String {
    val allHeaders = headers + listOf(
            "Accept-Language" to "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3",
            "User-Agent" to "ChatClient/1.0"
    )

    val request = HttpRequest.newBuilder()
            .uri(uri)
            .apply {
                allHeaders.forEach {
                    header(it.first, it.second.toString())
                }

                when (method) {
                    POST -> {
                        POST(HttpRequest.BodyPublishers
                                .ofString(params.joinToString("&") { "${it.first}=${it.second}" }))
                        header("Content-Type", "application/x-www-form-urlencoded")
                    }
                    GET -> GET()
                }
            }
            .build()

    val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build()

    val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))

    if (response.statusCode() != 200)
        throw RuntimeException(response.body()) //TODO дописать обработку ошибок

    return response.body()
}

