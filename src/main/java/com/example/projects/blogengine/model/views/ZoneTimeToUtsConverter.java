package com.example.projects.blogengine.model.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZoneTimeToUtsConverter implements AttributeConverter<Long, String> {
    private final Logger logger = LoggerFactory.getLogger(ZoneTimeToUtsConverter.class);
    @Override
    public String convertToDatabaseColumn(Long aLong) {
        //todo or not?
        return "";
    }

    @Override
    public Long convertToEntityAttribute(String zonedDateTime) {
        logger.info(String.valueOf((int) zonedDateTime.charAt(11)));
        //todo time settings
        return ZonedDateTime.
                of(LocalDateTime.parse(zonedDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        ZoneId.of("Australia/Brisbane")).toInstant().toEpochMilli();
    }
}
