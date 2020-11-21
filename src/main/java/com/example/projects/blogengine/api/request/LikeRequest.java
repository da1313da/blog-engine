package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeRequest {
    @JsonProperty("post_id")
    int postId;
}
