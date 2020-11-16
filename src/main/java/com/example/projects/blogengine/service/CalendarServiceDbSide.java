package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CalendarResponse;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.CalendarStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarServiceDbSide implements CalendarService {
    @Autowired
    private PostRepository postRepository;

    @Override
    public CalendarResponse getCalendarResponse(Integer year) {
        List<CalendarStatistics> postCountPerYear = postRepository.getPostCountPerDayInYear(year);
        List<Integer> years = postRepository.getYears();
        CalendarResponse response = new CalendarResponse();
        Map<String, Integer> posts = new HashMap<>();
        postCountPerYear.forEach(p -> posts.put(p.getDate(), p.getCount()));
        response.setPosts(posts);
        response.setYears(years);
        return response;
    }
}
