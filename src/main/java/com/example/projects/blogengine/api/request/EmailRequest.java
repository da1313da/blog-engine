package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailRequest {
    @JsonProperty("email")
    private String email;
}
