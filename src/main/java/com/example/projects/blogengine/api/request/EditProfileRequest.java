package com.example.projects.blogengine.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditProfileRequest {
    private String name;
    private String email;
    private String password;
    private Integer removePhoto;
}
