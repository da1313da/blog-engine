package com.example.projects.blogengine.service.interfaces;

import com.example.projects.blogengine.api.response.StatisticResponse;

public interface BlogStatisticService {
    StatisticResponse getBlog();
    StatisticResponse getUser(int id);
}
