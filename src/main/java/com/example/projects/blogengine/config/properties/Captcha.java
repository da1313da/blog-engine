package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class Captcha {
    @NotNull
    @Range(min = 30, max = 30)
    Integer secretCodeLength;
    @NotNull
    @Range(min = 5, max = 7)
    Integer displayCodeLength;
    @NotNull
    @Range(min = 100, max = 100)
    Integer captchaImageWidth;
    @NotNull
    @Range(min = 35, max = 35)
    Integer captchaImageHeight;
}
