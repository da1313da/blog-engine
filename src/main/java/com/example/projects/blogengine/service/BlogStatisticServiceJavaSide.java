package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.StatisticResponse;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.service.interfaces.BlogStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class BlogStatisticServiceJavaSide implements BlogStatisticService {

    @Autowired
    PostRepository postRepository;
    @Autowired
    GlobalSettingsRepository globalSettingsRepository;

    @Override
    public StatisticResponse getBlog() {
        StatisticResponse response = new StatisticResponse();
        Stream<Post> posts = postRepository.getPostsStreamFetchVotes();
        Optional<StatisticResponse> reduce = posts.map(post -> new StatisticResponse(1,
                post.getLikes().size(),
                post.getDisLikes().size(),
                post.getViewCount(),
                post.getTime().toEpochSecond()))
                .reduce((r1, r2) -> new StatisticResponse(
                        r1.getPostsCount() + r2.getPostsCount(),
                        r1.getLikesCount() + r2.getLikesCount(),
                        r1.getDislikesCount() + r2.getDislikesCount(),
                        r1.getViewsCount() + r2.getViewsCount(),
                        r1.getFirstPublication() < r2.getFirstPublication() ? r1.getFirstPublication() : r2.getFirstPublication()));

        return reduce.orElse(response);
    }

    @Override
    public StatisticResponse getUser(int id) {
        StatisticResponse response = new StatisticResponse();
        List<Post> postList = postRepository.getUserPostsFetchVotes(id);
        Optional<StatisticResponse> reduce = postList.stream()
                .map(post -> new StatisticResponse(
                1,
                post.getLikes().size(),
                post.getDisLikes().size(),
                post.getViewCount(),
                post.getTime().toEpochSecond()))
                .reduce((r1, r2) -> new StatisticResponse(
                    r1.getPostsCount() + r2.getPostsCount(),
                    r1.getLikesCount() + r2.getLikesCount(),
                    r1.getDislikesCount() + r2.getDislikesCount(),
                    r1.getViewsCount() + r2.getViewsCount(),
                              r1.getFirstPublication() < r2.getFirstPublication() ? r1.getFirstPublication() : r2.getFirstPublication()));

        return reduce.orElse(response);
    }
}
