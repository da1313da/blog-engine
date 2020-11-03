package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationErrors {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
