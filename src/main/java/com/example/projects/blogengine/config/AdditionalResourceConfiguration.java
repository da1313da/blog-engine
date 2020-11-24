package com.example.projects.blogengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceConfiguration implements WebMvcConfigurer {
    @Value("${uploadLocation}")
    private String uploadLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + uploadLocation + "/**").addResourceLocations("file:" + uploadLocation + "/");//todo path resolution
    }
}
