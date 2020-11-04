package com.example.projects.blogengine.data;

import com.example.projects.blogengine.utility.HtmlTagRemover;
import com.example.projects.blogengine.utility.ZdtToUnixTimestampConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.ZonedDateTime;

public interface PostForPostResponse {
    Integer getId();

    @JsonProperty("timestamp")
    @JsonSerialize(converter = ZdtToUnixTimestampConverter.class)
    ZonedDateTime getTime();

    UserForResponse getUser();

    String getTitle();

    @JsonProperty("announce")
    @JsonSerialize(converter = HtmlTagRemover.class)
    String getText();

    Integer getLikeCount();

    Integer getDislikeCount();

    Integer getViewCount();

    Integer getCommentCount();
}
