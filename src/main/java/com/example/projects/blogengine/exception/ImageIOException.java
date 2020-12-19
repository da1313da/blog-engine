package com.example.projects.blogengine.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ImageIOException extends RuntimeException{

    private final HttpStatus httpStatus;
    private Exception exception;


    public ImageIOException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

}
