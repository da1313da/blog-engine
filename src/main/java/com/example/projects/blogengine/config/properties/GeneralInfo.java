package com.example.projects.blogengine.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeneralInfo {
    String title;
    String subtitle;
    String phone;
    String email;
    String copyright;
    String copyrightFrom;
}
