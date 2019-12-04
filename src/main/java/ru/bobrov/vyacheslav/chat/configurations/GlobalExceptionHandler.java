package ru.bobrov.vyacheslav.chat.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.*;
import ru.bobrov.vyacheslav.chat.services.Translator;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private Translator translator;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<?> resourceExists(ResourceExistsException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<?> notImplemented(NotImplementedException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentials(BadCredentialsException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied(AccessDeniedException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<?> illegalOperation(IllegalOperationException ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> globalExceptionHandler(Throwable ex, WebRequest request) {
        return createResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> createResponse(Throwable ex, WebRequest request, HttpStatus httpStatus) {
        final String title;
        if (ex instanceof ChatExceptions)
            title = ((ChatExceptions) ex).getTitle();
        else
            title = translator.translate("internal-server-error");

        ErrorDetails details = ErrorDetails.builder()
                .timestamp(new Date())
                .title(title)
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return new ResponseEntity<>(details, httpStatus);
    }
}
