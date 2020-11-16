package com.example.projects.blogengine.api.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CalendarResponse {
    List<Integer> years;
    Map<String, Integer> posts;
}
