package com.example.projects.blogengine.api.response;

import lombok.Data;

import java.util.List;

@Data
public class TagsListResponse {
    private List<TagWeightResponse> tags;
}
