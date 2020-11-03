package com.example.projects.blogengine.data;

import com.example.projects.blogengine.utility.ZdtToUnixTimestampConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.ZonedDateTime;

public interface CommentForPostById {
    Integer getId();
    @JsonProperty("timestamp")
    @JsonSerialize(converter = ZdtToUnixTimestampConverter.class)
    ZonedDateTime getTime();
    String getText();
    UserForResponse getUser();
}
