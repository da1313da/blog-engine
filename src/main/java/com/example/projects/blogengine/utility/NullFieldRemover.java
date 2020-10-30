package com.example.projects.blogengine.utility;

import com.fasterxml.jackson.databind.util.StdConverter;

public class NullFieldRemover extends StdConverter<Object, Object> {

    @Override
    public Object convert(Object o) {
        return o;
    }
}
