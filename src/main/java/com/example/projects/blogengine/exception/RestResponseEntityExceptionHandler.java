package com.example.projects.blogengine.exception;

import com.example.projects.blogengine.api.response.AddImageErrorsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<AddImageErrorsResponse> sizeLimitException(MaxUploadSizeExceededException exception){
        AddImageErrorsResponse response = new AddImageErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("image", "Размер файла превышает допустимый размер");
        response.setResult(false);
        response.setErrors(errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
