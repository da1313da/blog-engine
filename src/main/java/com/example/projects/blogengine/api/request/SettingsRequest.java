package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsRequest {
    @JsonProperty("MULTIUSER_MODE")
    boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    boolean postPreModeration;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    boolean statisticIsPublic;
}
