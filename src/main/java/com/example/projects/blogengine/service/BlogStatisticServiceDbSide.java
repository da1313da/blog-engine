package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.StatisticResponse;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.PostsStatistics;
import com.example.projects.blogengine.service.interfaces.BlogStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
public class BlogStatisticServiceDbSide implements BlogStatisticService {
    @Autowired
    private PostRepository postRepository;

    @Override
    public StatisticResponse getBlog() {
        PostsStatistics statistics = postRepository.getGlobalStatistics();
        StatisticResponse response = new StatisticResponse();
        response.setPostsCount(statistics.getPostCount() == null ? 0 : statistics.getPostCount());
        response.setLikesCount(statistics.getLikesCount() == null ? 0 : statistics.getLikesCount());
        response.setDislikesCount(statistics.getDislikesCount() == null ? 0 : statistics.getDislikesCount());
        response.setViewsCount(statistics.getViewsCount() == null ? 0 : statistics.getViewsCount());
        response.setFirstPublication(statistics.getFirstPublication() == null ? null : statistics.getFirstPublication().toEpochSecond(ZoneOffset.UTC));
        return response;
    }

    @Override
    public StatisticResponse getUser(int id) {
        PostsStatistics statistics = postRepository.getUserStatistics(id);
        StatisticResponse response = new StatisticResponse();
        response.setPostsCount(statistics.getPostCount() == null ? 0 : statistics.getPostCount());
        response.setLikesCount(statistics.getLikesCount() == null ? 0 : statistics.getLikesCount());
        response.setDislikesCount(statistics.getDislikesCount() == null ? 0 : statistics.getDislikesCount());
        response.setViewsCount(statistics.getViewsCount() == null ? 0 : statistics.getViewsCount());
        response.setFirstPublication(statistics.getFirstPublication() == null ? null : statistics.getFirstPublication().toEpochSecond(ZoneOffset.UTC));
        return response;
    }
}
