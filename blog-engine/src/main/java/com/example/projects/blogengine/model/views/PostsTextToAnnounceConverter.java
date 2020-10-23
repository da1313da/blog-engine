package com.example.projects.blogengine.model.views;

import org.jsoup.Jsoup;

import javax.persistence.AttributeConverter;

public class PostsTextToAnnounceConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String s) {
        return s;
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return Jsoup.parse(s).text();
    }
}
