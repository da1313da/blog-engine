package com.example.projects.blogengine.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePasswordErrors {

    String code;

    String password;

    String captcha;

}
