package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.unit.DataSize;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class Upload {
    @NotEmpty
    @NotNull
    private String location;
    @NotNull
    private DataSize maxImageSize;
    @NotNull
    private DataSize maxPhotoSize;
}
