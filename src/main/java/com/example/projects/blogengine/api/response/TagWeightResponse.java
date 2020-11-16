package com.example.projects.blogengine.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagWeightResponse {
    String name;
    double weight;
}
