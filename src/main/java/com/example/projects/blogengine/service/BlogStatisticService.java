package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.StatisticResponse;
import com.example.projects.blogengine.exception.SettingsNotFoundException;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.PostsStatistics;
import com.example.projects.blogengine.security.UserDetailsImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
public class BlogStatisticService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private ModelMapper modelMapper;

    public StatisticResponse getBlog(UserDetailsImpl userDetails) {
        GlobalSettings settings = globalSettingsRepository.getByCode("STATISTICS_IS_PUBLIC").orElseThrow(SettingsNotFoundException::new);
        if (settings.getValue().equals("NO") && userDetails.getUser().getIsModerator() != 1){
            return null;
        } else {
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
    }

    public StatisticResponse getUser(UserDetailsImpl userDetails) {
        StatisticResponse response = new StatisticResponse();
        List<Post> postList = postRepository.getUserPostsFetchVotes(userDetails.getUser().getId());
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

    public StatisticResponse getBlog1(UserDetailsImpl userDetails) {
        GlobalSettings settings = globalSettingsRepository.getByCode("STATISTICS_IS_PUBLIC").orElseThrow(SettingsNotFoundException::new);
        if (settings.getValue().equals("NO") && userDetails.getUser().getIsModerator() != 1) {
            return null;
        } else {
            PostsStatistics statistics = postRepository.getGlobalStatistics();
            System.out.println(statistics.getPostsCount());
            return modelMapper.map(statistics, StatisticResponse.class);
        }
    }

    public StatisticResponse getUser1(UserDetailsImpl userDetails) {
        PostsStatistics statistics = postRepository.getUserStatistics(userDetails.getUser().getId());
        return modelMapper.map(statistics, StatisticResponse.class);
    }
}
