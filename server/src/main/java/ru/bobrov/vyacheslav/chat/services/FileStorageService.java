package ru.bobrov.vyacheslav.chat.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.FileNotFoundException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.FileStorageException;
import ru.bobrov.vyacheslav.chat.services.utils.Translator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

/**
 * Сервис для работы с файлами
 */
@Service
@RequiredArgsConstructor(access = PUBLIC)
@FieldDefaults(level = PRIVATE)
@Slf4j
@Transactional
public class FileStorageService {
    @NonNull
    final UserService userService;
    @NonNull
    final Translator translator;

    @Value("${file-storage.upload-dir}")
    String uploadDir;

    public UUID storeFile(UUID userId, MultipartFile file) {
        try {
            val fileId = userService.addFileToUser(userId, file.getContentType());
            val fileLocation = getStorageLocation().resolve(fileId.toString());
            Files.copy(file.getInputStream(), fileLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileId;
        } catch (Exception ex) {
            log.error(format("File save error, uploadDir: %s, file: %s", uploadDir, file.getName()), ex);
            throw new FileStorageException(
                    translator.translate("file-storage-exception-title"),
                    translator.translate("file-storage-exception")
            );
        }
    }

    public Resource loadFile(UUID fileId) {
        try {
            val fileLocation = getStorageLocation().resolve(fileId.toString()).normalize();
            val resource = new UrlResource(fileLocation.toUri());
            if (resource.exists())
                return resource;
            throw new FileNotFoundException(
                    translator.translate("file-not-found-title"),
                    translator.translate("file-not-found", fileId)
            );
        } catch (Exception ex) {
            log.error(format("File load error, uploadDir: %s, fileId: %s", uploadDir, fileId), ex);
            throw new FileNotFoundException(
                    translator.translate("file-not-found-title"),
                    translator.translate("file-not-found", fileId)
            );
        }
    }

    public void dropFile(UUID fileId) {
        try {
            userService.deleteFile(fileId);
            val fileLocation = getStorageLocation().resolve(fileId.toString()).normalize();
            if (!Files.exists(fileLocation))
                return;
            Files.delete(fileLocation);
        } catch (Exception ex) {
            log.error(format("File delete error, uploadDir: %s, fileId: %s", uploadDir, fileId), ex);
            throw new FileStorageException(
                    translator.translate("file-delete-exception-title"),
                    translator.translate("file-delete-exception")
            );
        }
    }

    private Path getStorageLocation() {
        try {
            val storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(storageLocation);
            return storageLocation;
        } catch (Exception ex) {
            log.error(format("Create storage directory: %s error", uploadDir), ex);
            throw new FileStorageException(
                    translator.translate("file-storage-exception-title"),
                    translator.translate("file-storage-exception")
            );
        }
    }
}
