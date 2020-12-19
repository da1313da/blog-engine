package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@Setter
@ToString
public class Captcha {
    @NotNull
    @Range(min = 30, max = 30)
    private Integer secretCodeLength;
    @NotNull
    @Range(min = 5, max = 7)
    private Integer displayCodeLength;
    @NotNull
    @Range(min = 100, max = 100)
    private Integer captchaImageWidth;
    @NotNull
    @Range(min = 35, max = 35)
    private Integer captchaImageHeight;
    private Duration deleteTime;
}
