package com.example.projects.blogengine.api.response;

import lombok.Data;

@Data
public class GeneralInfoResponse {
    private String title;
    private String subtitle;
    private String phone;
    private String email;
    private String copyright;
    private String copyrightFrom;
}
