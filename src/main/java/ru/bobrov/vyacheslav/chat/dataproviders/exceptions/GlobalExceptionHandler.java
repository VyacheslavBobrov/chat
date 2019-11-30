package ru.bobrov.vyacheslav.chat.dataproviders.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

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
