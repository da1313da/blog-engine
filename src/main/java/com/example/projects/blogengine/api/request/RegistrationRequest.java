package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationRequest {
    @JsonProperty("e_mail")
    String email;
    String password;
    String name;
    String captcha;
    @JsonProperty("captcha_secret")
    String captchaSecret;
}
