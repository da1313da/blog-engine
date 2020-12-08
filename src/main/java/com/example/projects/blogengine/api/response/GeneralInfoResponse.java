package com.example.projects.blogengine.api.response;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class GeneralInfoResponse {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;
}
