package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.StatisticResponse;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.service.interfaces.BlogStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class BlogStatisticServiceJavaSideVar2 implements BlogStatisticService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public StatisticResponse getBlog() {
        StatisticResponse response = new StatisticResponse();
        Stream<Post> posts = postRepository.getPostsStreamFetchVotes();
        posts.forEach(post -> {
            response.setPostsCount(response.getPostsCount() + 1);
            response.setLikesCount(response.getLikesCount() + post.getLikes().size());
            response.setDislikesCount(response.getDislikesCount() + post.getDisLikes().size());
            response.setViewsCount(response.getViewsCount() + post.getViewCount());
            if (response.getFirstPublication() == null){
                response.setFirstPublication(post.getTime().toEpochSecond());
            } else {
                long t = post.getTime().toEpochSecond();
                if (t < response.getFirstPublication()){
                    response.setFirstPublication(t);
                }
            }
        });
        return response;
    }

    @Override
    public StatisticResponse getUser(int id) {
        StatisticResponse response = new StatisticResponse();
        List<Post> posts = postRepository.getUserPostsFetchVotes(id);
        posts.forEach(post -> {
            response.setPostsCount(response.getPostsCount() + 1);
            response.setLikesCount(response.getLikesCount() + post.getLikes().size());
            response.setDislikesCount(response.getDislikesCount() + post.getDisLikes().size());
            response.setViewsCount(response.getViewsCount() + post.getViewCount());
            if (response.getFirstPublication() == null){
                response.setFirstPublication(post.getTime().toEpochSecond());
            } else {
                long t = post.getTime().toEpochSecond();
                if (t < response.getFirstPublication()){
                    response.setFirstPublication(t);
                }
            }
        });
        return response;
    }
}
