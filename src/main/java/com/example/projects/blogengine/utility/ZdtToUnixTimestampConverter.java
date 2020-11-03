package com.example.projects.blogengine.utility;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.ZonedDateTime;

public class ZdtToUnixTimestampConverter extends StdConverter<ZonedDateTime, Long> {
    @Override
    public Long convert(ZonedDateTime dateTime) {
        return dateTime.toEpochSecond();
    }
}
