package com.example.projects.blogengine.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {
    long postsCount;
    long likesCount;
    long dislikesCount;
    long viewsCount;
    Long firstPublication;
}
