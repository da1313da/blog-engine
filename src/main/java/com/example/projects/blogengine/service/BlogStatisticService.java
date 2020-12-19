package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.StatisticResponse;
import com.example.projects.blogengine.exception.AccessDeniedException;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

//todo check in db for user?
@Service
public class BlogStatisticService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    public StatisticResponse getUser(UserDetailsImpl userDetails) {
        int userId = userDetails.getUser().getId();
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "time"));
        List<ZonedDateTime> firstPublicationTime = postRepository.getUserPostFirstPublicationTime(userId, pageable);
        Long firstPublicationTimeStamp = firstPublicationTime.isEmpty() ? null : firstPublicationTime.get(0).toEpochSecond();
        int postsCount = postRepository.getUserPostCount(userId);
        int allLikesCount = postRepository.getUserLikesCount(userId);
        int allDislikesCount = postRepository.getUserDislikesCount(userId);
        int allViewCount = postRepository.getUserViewCount(userId);
        StatisticResponse statisticResponse = new StatisticResponse();
        statisticResponse.setFirstPublication(firstPublicationTimeStamp);
        statisticResponse.setViewsCount(allViewCount);
        statisticResponse.setDislikesCount(allDislikesCount);
        statisticResponse.setLikesCount(allLikesCount);
        statisticResponse.setPostsCount(postsCount);
        return statisticResponse;

    }

    public StatisticResponse getBlog(UserDetailsImpl userDetails){
        GlobalSettings settings = globalSettingsRepository.getByCode("STATISTICS_IS_PUBLIC")
                .orElseThrow(() -> new NotFoundException("Global settings not found!", HttpStatus.BAD_REQUEST));
        if (settings.getValue().equals("NO") && userDetails.getUser().getIsModerator() != 1) {
            throw new AccessDeniedException("Global statistics available only for users with moderator authorities", HttpStatus.UNAUTHORIZED);
        } else {
            Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "time"));
            List<ZonedDateTime> firstPublicationTime = postRepository.getAllPostFirstPublicationTime(pageable);
            Long firstPublicationTimeStamp = firstPublicationTime.isEmpty() ? null : firstPublicationTime.get(0).toEpochSecond();
            int postsCount = postRepository.getPostsCount();
            int allLikesCount = postRepository.getAllLikesCount();
            int allDislikesCount = postRepository.getAllDislikesCount();
            int allViewCount = postRepository.getAllViewCount();
            StatisticResponse statisticResponse = new StatisticResponse();
            statisticResponse.setFirstPublication(firstPublicationTimeStamp);
            statisticResponse.setViewsCount(allViewCount);
            statisticResponse.setDislikesCount(allDislikesCount);
            statisticResponse.setLikesCount(allLikesCount);
            statisticResponse.setPostsCount(postsCount);
            return statisticResponse;
        }
    }
}
