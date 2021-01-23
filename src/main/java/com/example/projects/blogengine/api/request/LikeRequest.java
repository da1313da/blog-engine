package com.example.projects.blogengine.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeRequest {
    @JsonProperty("post_id")
    private int postId;
}
