package com.example.projects.blogengine.api.response;

import com.example.projects.blogengine.data.UserForLoginDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    Boolean result;
    UserForLoginDto user;
}
