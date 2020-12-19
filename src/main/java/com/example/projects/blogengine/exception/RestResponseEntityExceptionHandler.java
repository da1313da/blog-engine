package com.example.projects.blogengine.exception;

import com.example.projects.blogengine.api.response.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<?> notFoundExceptionHandler(NotFoundException notFoundException){
        Map<String, String> body = new HashMap<>();
        body.put("message", notFoundException.getMessage());
        return ResponseEntity.status(notFoundException.getHttpStatus()).body(body);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(AccessDeniedException accessDeniedException){
        return ResponseEntity.status(accessDeniedException.getHttpStatus()).build();
    }

    @ExceptionHandler(value = ImageIOException.class)
    public ResponseEntity<?> imageUploadExceptionHandler(ImageIOException exception){
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("image", exception.getMessage());
        response.setResult(false);
        response.setErrors(errors);
        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

    @ExceptionHandler(value = InternalException.class)
    public ResponseEntity<?> internalExceptionHandler(InternalException internalException){
        Map<String, String> body = new HashMap<>();
        body.put("message", internalException.getMessage());
        return ResponseEntity.status(internalException.getHttpStatus()).body(body);
    }
}
