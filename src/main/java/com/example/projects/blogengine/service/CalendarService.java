package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CalendarResponse;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {
    @Autowired
    private PostRepository postRepository;

    public CalendarResponse getCalendarResponse(Integer year) {
        CalendarResponse response = new CalendarResponse();
        List<CalendarStatistics> statistics = postRepository.getPostCountPerDay(year);
        List<Integer> years = postRepository.getYears();
        Map<String, Integer> posts = new HashMap<>();
        statistics.forEach(p -> {
            String formatted = Arrays.stream(p.getDate()
                    .split("-"))
                    .map(t -> String.format("%02d", Integer.parseInt(t)))
                    .reduce((t1, t2) -> t1 + "-" + t2).orElse("");
            posts.put(formatted, p.getCount());
        });
        response.setPosts(posts);
        response.setYears(years);

        return response;
    }

}
