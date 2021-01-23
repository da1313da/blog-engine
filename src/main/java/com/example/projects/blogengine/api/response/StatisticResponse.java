package com.example.projects.blogengine.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {
    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private Long firstPublication;
}
