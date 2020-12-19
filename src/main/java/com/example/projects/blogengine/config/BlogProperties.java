package com.example.projects.blogengine.config;

import com.example.projects.blogengine.config.properties.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "blog")
@Validated
public class BlogProperties {
    @NotNull
    @Valid
    private Upload upload;
    @NotNull
    @Valid
    private GeneralInfo generalInfo;
    @NotNull
    @Valid
    private Account account;
    @NotNull
    @Valid
    private Captcha captcha;
    @NotNull
    @Valid
    private Post post;
    @NotNull
    @NotEmpty
    private String emailAddress;
    @NotNull
    @NotEmpty
    private String hostPath;
}
