package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CalendarResponse;

public interface CalendarService {
    CalendarResponse getCalendarResponse(Integer year);
}
