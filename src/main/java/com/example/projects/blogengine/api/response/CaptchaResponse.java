package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaptchaResponse {

    String secret;

    String image;

}
