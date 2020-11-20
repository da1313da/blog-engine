package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationErrorsResponse {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
