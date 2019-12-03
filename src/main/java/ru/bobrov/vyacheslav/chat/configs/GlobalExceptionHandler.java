package ru.bobrov.vyacheslav.chat.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ErrorDetails;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.NotImplementedException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ResourceExistsException;
import ru.bobrov.vyacheslav.chat.dataproviders.exceptions.ResourceNotFoundException;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<?> resourceExists(ResourceExistsException ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotImplementedException.class)
    public ResponseEntity<?> notImplemented(NotImplementedException ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentials(BadCredentialsException ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> globalExceptionHandler(Throwable ex, WebRequest request) {
        return new ResponseEntity<>(fillDetails(ex, request), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDetails fillDetails(Throwable ex, WebRequest request) {
        return ErrorDetails.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();
    }
}
