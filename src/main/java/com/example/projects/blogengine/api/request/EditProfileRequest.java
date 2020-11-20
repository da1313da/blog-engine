package com.example.projects.blogengine.api.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class EditProfileRequest {
    String name;
    String email;
    String password;
    Integer removePhoto;
    MultipartFile photo;
}
