package com.example.projects.blogengine.api.response;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class GeneralInfoResponse {
    @Value("${GeneralInfo.title}")
    private String title;
    @Value("${GeneralInfo.subtitle}")
    private String subtitle;
    @Value("${GeneralInfo.phone}")
    private String phone;
    @Value("${GeneralInfo.email}")
    private String email;
    @Value("${GeneralInfo.copyright}")
    private String copyright;
    @Value("${GeneralInfo.copyrightFrom}")
    private String copyrightFrom;
}
