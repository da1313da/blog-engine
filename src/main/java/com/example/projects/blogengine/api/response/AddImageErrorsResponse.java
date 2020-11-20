package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AddImageErrorsResponse {
    boolean result;
    Map<String, String> errors;
}
