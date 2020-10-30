package com.example.projects.blogengine.api.response;

import com.example.projects.blogengine.data.UserForLoginResponse;
import com.example.projects.blogengine.utility.NullFieldRemover;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    Boolean result;

    @JsonSerialize(converter = NullFieldRemover.class)
    UserForLoginResponse user;

}
