package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class Post {
    @NotNull
    @Range(min = 3)
    private Integer minTitleSize;
    @NotNull
    @Range(min = 50)
    private Integer minTextSize;
    @NotNull
    @Range(min = 3)
    private Integer minCommentSize;
}
