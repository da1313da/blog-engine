package com.example.projects.blogengine.utility;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.jsoup.Jsoup;

public class HtmlTagRemover extends StdConverter<String, String> {
    @Override
    public String convert(String s) {
        return Jsoup.parse(s).text();
    }
}
