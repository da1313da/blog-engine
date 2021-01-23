package com.example.projects.blogengine.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TagWeightResponse {
    private String name;
    private double weight;
}
