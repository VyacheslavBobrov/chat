package ru.bobrov.vyacheslav.chat.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bobrov.vyacheslav.chat.controllers.models.response.UploadFileApiModel;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;
import static ru.bobrov.vyacheslav.chat.services.Utils.toDo;

@Api("Files management system")
@RestController
@AllArgsConstructor(access = PUBLIC)
@RequestMapping("/api/v1/file")
@FieldDefaults(level = PRIVATE)
@Slf4j
@CrossOrigin
public class FileController {
    @ApiOperation(value = "Upload file to storage", response = UploadFileApiModel.class)
    @PostMapping("/upload")
    public UploadFileApiModel upload(
            @RequestParam MultipartFile file,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST file from %s, size: %d, type: %s",
                header.getHost(), file.getSize(), file.getContentType()));

        return toDo();
    }

    @ApiOperation(
            value = "Upload Multiple file to storage",
            response = UploadFileApiModel.class,
            responseContainer = "List"
    )
    @PostMapping("/uploadMultiple")
    public List<UploadFileApiModel> uploadMultiple(
            @RequestParam MultipartFile[] files,
            @RequestHeader HttpHeaders header
    ) {
        log.info(format("POST files from %s, number of files: %d",
                header.getHost(), files.length));

        return Arrays.stream(files).map(file -> upload(file, header)).collect(Collectors.toUnmodifiableList());
    }

    @ApiOperation(value = "Download file from storage", response = Resource.class, responseContainer = "ResponseEntity")
    @GetMapping("/{fileId}")
    ResponseEntity<Resource> download(
            @PathVariable UUID fileId,
            HttpServletRequest request
    ) {
        log.info(format("GET file from %s, fileId: %s", request.getRemoteHost(), fileId));

        Resource resource = toDo();

        String contentType = "application/octet-stream";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .body(resource);
    }
}
