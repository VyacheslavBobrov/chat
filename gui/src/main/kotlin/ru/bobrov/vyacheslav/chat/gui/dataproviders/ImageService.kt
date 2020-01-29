package ru.bobrov.vyacheslav.chat.gui.dataproviders

import javafx.scene.image.Image
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.io.InputStream
import java.lang.ref.SoftReference
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

private const val FILES_PATH = "/api/v1/file"

private const val AUTHORIZATION_HEADER = "Authorization"
private const val AUTHORIZATION_PREF = "Bearer"

@Service
class ImageService(
        @Value("\${chatserver.host}")
        private val host: String,
        private val authenticationService: AuthenticationService
) {
    @Value("\${file-storage.upload-dir}")
    private var uploadDir: String? = null

    private val filesPath = "$host$FILES_PATH"

    private val imagesMap: MutableMap<UUID, SoftReference<Image>> = mutableMapOf()

    fun getImage(id: UUID): Image {
        val imageRef = imagesMap[id]
        if (imageRef?.get() != null)
            return imageRef.get()!!

        val imageFromStorage = loadImageFromStorage(id)
        if (imageFromStorage != null) {
            imagesMap[id] = SoftReference(imageFromStorage)
            return imageFromStorage
        }

        val imageIs = loadImageFromServer(id)
        val imageFromServer = Image(imageIs)
        saveImage(id, imageIs)
        imagesMap[id] = SoftReference(imageFromServer)
        return imageFromServer
    }

    private fun loadImageFromServer(id: UUID): InputStream {
        val allHeaders = listOf(
                AUTHORIZATION_HEADER to "$AUTHORIZATION_PREF ${authenticationService.user.jwtToken}",
                "Accept-Language" to "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3",
                "User-Agent" to "ChatClient/1.0"
        )

        val request = HttpRequest.newBuilder()
                .uri(URI.create("$filesPath/$id"))
                .apply {
                    allHeaders.forEach {
                        header(it.first, it.second)
                    }
                }
                .GET()
                .build()

        val httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())

        if (response.statusCode() != 200)
            throw RuntimeException("Image download error, code: ${response.statusCode()}")

        return response.body()
    }

    private fun loadImageFromStorage(id: UUID): Image? {
        val fileLocation: Path = getStorageLocation().resolve(id.toString()).normalize()
        val resource: Resource = UrlResource(fileLocation.toUri())
        return if (resource.exists()) Image(resource.inputStream) else null
    }

    private fun saveImage(id: UUID, stream: InputStream) {
        val fileLocation: Path = getStorageLocation().resolve(id.toString())
        Files.copy(stream, fileLocation, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun getStorageLocation(): Path {
        val storageLocation: Path = Paths.get(uploadDir!!).toAbsolutePath().normalize()
        Files.createDirectories(storageLocation)
        return storageLocation
    }
}