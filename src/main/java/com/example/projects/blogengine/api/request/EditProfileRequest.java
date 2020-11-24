package com.example.projects.blogengine.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EditProfileRequest {
    String name;
    String email;
    String password;
    Integer removePhoto;
}
