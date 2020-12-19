package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class Account {
    @NotNull
    @Range(min = 36, max = 36)
    private Integer avatarImageWidth;
    @NotNull
    @Range(min = 36, max = 36)
    private Integer avatarImageHeight;
    @NotNull
    @Range(min = 6, max = 30)
    private Integer passwordLength;
    @NotNull
    @Range(min = 45, max = 45)
    private Integer passwordRestoreTokenLength;
}
