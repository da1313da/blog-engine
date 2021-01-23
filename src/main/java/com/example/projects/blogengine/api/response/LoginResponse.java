package com.example.projects.blogengine.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private boolean result;
    private UserLoginResponse user;
}
