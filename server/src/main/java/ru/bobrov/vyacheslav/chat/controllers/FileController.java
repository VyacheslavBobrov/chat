package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bobrov.vyacheslav.chat.dto.response.UploadFileApiModel;
import ru.bobrov.vyacheslav.chat.services.FileStorageService;
import ru.bobrov.vyacheslav.chat.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@Api("Files management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/file")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
@CrossOrigin
@Transactional
public class FileController {
    FileStorageService fileStorageService;
    UserService userService;

    @PreAuthorize("@userFilesSecurityPolicy.canUploadFile(principal, #userId)")
    @ApiOperation(value = "Upload file to storage", response = UploadFileApiModel.class)
    @PostMapping("/upload")
    public UploadFileApiModel upload(
            @RequestParam UUID userId,
            @RequestParam MultipartFile file,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST file from %s, userId: %s, size: %d, type: %s",
                header.getHost(), userId, file.getSize(), file.getContentType()));

        val fileId = fileStorageService.storeFile(userId, file);

        return UploadFileApiModel.builder()
                .fileUUID(fileId)
                .fileDownloadUri("/api/v1/file/" + fileId.toString())
                .fileType(file.getContentType())
                .size(file.getSize())
                .build();
    }

    @PreAuthorize("@userFilesSecurityPolicy.canUploadFile(principal, #userId)")
    @ApiOperation(
            value = "Upload Multiple file to storage",
            response = UploadFileApiModel.class,
            responseContainer = "List"
    )
    @PostMapping("/uploadMultiple")
    public List<UploadFileApiModel> uploadMultiple(
            @RequestPart UUID userId,
            @RequestPart MultipartFile[] files,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST files from %s, userId: %s, number of files: %d",
                header.getHost(), userId, files.length));

        return Arrays.stream(files).map(file -> upload(userId, file, header)).collect(Collectors.toUnmodifiableList());
    }

    @ApiOperation(value = "Download file from storage", response = Resource.class)
    @GetMapping("/{fileId}")
    ResponseEntity<Resource> download(
            @PathVariable UUID fileId,
            HttpServletRequest request
    ) {
        log.info(format("GET file from %s, fileId: %s", request.getRemoteHost(), fileId));

        val resource = fileStorageService.loadFile(fileId);
        val userFile = userService.findFileById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(userFile.getFileMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .body(resource);
    }

    @PreAuthorize("@userFilesSecurityPolicy.canGetFilesIdsForUser(principal, #userId)")
    @ApiOperation(value = "Get files ids for user", response = UUID.class, responseContainer = "List")
    @GetMapping
    List<UUID> getFilesIdsForUser(
            @RequestParam UUID userId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("GET file ids for userId: %s, from %s", userId, header.getHost()));
        return userService.getFilesIdsForUser(userId);
    }

    @PreAuthorize("@userFilesSecurityPolicy.canDropFile(principal, #fileId)")
    @ApiOperation(value = "Drop file from storage", response = UUID.class, responseContainer = "List")
    @DeleteMapping("/{fileId}")
    List<UUID> dropFileFromStorage(
            @PathVariable UUID fileId,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("DEL file from %s, fileId: %s", header.getHost(), fileId));
        val user = userService.findUserByFileId(fileId);
        fileStorageService.dropFile(fileId);

        return userService.getFilesIdsForUser(user.getUserId());
    }
}
