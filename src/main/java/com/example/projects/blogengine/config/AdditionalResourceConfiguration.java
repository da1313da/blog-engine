package com.example.projects.blogengine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceConfiguration implements WebMvcConfigurer {

    @Autowired
    private BlogProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadLocation = properties.getUpload().getLocation();
        registry.addResourceHandler("/" + uploadLocation + "/**")
                .addResourceLocations("file:" + uploadLocation + "/");
        registry.addResourceHandler("/edit/" + uploadLocation + "/**")
                .addResourceLocations("file:" + uploadLocation + "/");
        registry.addResourceHandler("/post/" + uploadLocation + "/**")
                .addResourceLocations("file:" + uploadLocation + "/");
    }
}
