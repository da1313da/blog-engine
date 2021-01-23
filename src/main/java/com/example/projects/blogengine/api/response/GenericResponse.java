package com.example.projects.blogengine.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse {
    private boolean result;
    private Map<String, String> errors;
}
