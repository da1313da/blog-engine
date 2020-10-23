package com.example.projects.blogengine.model.views;

import javax.persistence.AttributeConverter;

public class NullToZeroConverter implements AttributeConverter<Integer, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Integer integer) {
        return integer;
    }

    @Override
    public Integer convertToEntityAttribute(Integer integer) {
        return integer == null ? 0 : integer;
    }
}
