package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    String code;
    String password;
    String captcha;
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
