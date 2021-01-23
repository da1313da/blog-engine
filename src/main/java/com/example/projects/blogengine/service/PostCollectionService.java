package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.PostAnnounceResponse;
import com.example.projects.blogengine.api.response.PostListResponse;
import com.example.projects.blogengine.exception.InternalException;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.projections.PostWithStatistics;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostCollectionService {

    public static final List<ModerationType> ALL_MODERATION_TYPES = List.of(ModerationType.NEW, ModerationType.ACCEPTED, ModerationType.DECLINED);

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostListResponse getPostList(int limit, int offset, String mode){
        List<Post> posts;
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (mode) {
            case "popular":
                posts = postRepository.getPopularPosts(page);
                break;
            case "best":
                posts = postRepository.getBestPost(page);
                break;
            case "early":
                posts = postRepository.getEarlyPosts(page);
                break;
            default:
                posts = postRepository.getRecentPosts(page);
                break;
        }
        response.setCount(postRepository.getPostCount());
        response.setPosts(convertToPostAnnounceList(posts));
        return response;
    }

    public PostListResponse getPostListByDate(int limit, int offset, String date) {
        PostListResponse response = new PostListResponse();
        try{
            String startTime = date + "T00:00:00";
            String endTime = date + "T23:59:59";
            ZonedDateTime startDate = ZonedDateTime.of(LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneId.of("UTC"));
            ZonedDateTime endDate = ZonedDateTime.of(LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME), ZoneId.of("UTC"));
            Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
            response.setCount(postRepository.getPostsCountByDate(startDate, endDate));
            List<Post> postsByDate = postRepository.getPostsByDate(startDate, endDate, page);
            response.setPosts(convertToPostAnnounceList(postsByDate));
        }catch (DateTimeParseException e){
            throw new InternalException(date + " parse error!", HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    public PostListResponse getPostListByTag(int limit, int offset, String tag) {
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        response.setCount(postRepository.getPostsCountByTag(tag));
        response.setPosts(convertToPostAnnounceList(postRepository.getPostsByTag(tag, page)));
        return response;
    }

    public PostListResponse getUserPosts(int offset, int limit, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        List<PostWithStatistics> posts;
        int postCount;
        switch (status){
            case "inactive":
                postCount = postRepository.getUserPostCount(user.getUser(), ALL_MODERATION_TYPES, (byte) 0);
                posts = postRepository.getUserPosts(user.getUser(), ALL_MODERATION_TYPES, (byte) 0, page);
                break;
            case "pending":
                postCount = postRepository.getUserPostCount(user.getUser(), List.of(ModerationType.NEW), (byte) 1);
                posts = postRepository.getUserPosts(user.getUser(), List.of(ModerationType.NEW), (byte) 1, page);
                break;
            case "declined":
                postCount = postRepository.getUserPostCount(user.getUser(), List.of(ModerationType.DECLINED), (byte) 1);
                posts = postRepository.getUserPosts(user.getUser(), List.of(ModerationType.DECLINED), (byte) 1, page);
                break;
            case "published":
                postCount = postRepository.getUserPostCount(user.getUser(), List.of(ModerationType.ACCEPTED), (byte) 1);
                posts = postRepository.getUserPosts(user.getUser(), List.of(ModerationType.ACCEPTED), (byte) 1, page);
                break;
            default:
                postCount = 0;
                posts = new ArrayList<>();
        }
        response.setPosts(posts.stream()
                .map(ps -> modelMapper.map(ps, PostAnnounceResponse.class)).collect(Collectors.toList()));
        response.setCount(postCount);
        return response;
    }

    public PostListResponse getPostsByQuery(int limit, int offset, String query) {
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        PostListResponse response = new PostListResponse();
        response.setCount(postRepository.getPostListCountBySearchWord(query));
        response.setPosts(convertToPostAnnounceList(postRepository.getPostListBySearchWord(query, page)));
        return response;
    }

    public PostListResponse getPostListToModeration(int limit, int offset, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        List<PostWithStatistics> posts;
        int postCount;
        switch (status){
            case "new":
                posts = postRepository.getNewActivePosts(page);
                postCount = postRepository.countByIsActiveAndModerationStatus((byte) 1, ModerationType.NEW);
                break;
            case "declined":
                posts = postRepository.getPostsModeratedByUser(user.getUser(), ModerationType.DECLINED, page);
                postCount = postRepository.getPostsModeratedByUserCount(user.getUser(), ModerationType.DECLINED);
                break;
            case "accepted":
                posts = postRepository.getPostsModeratedByUser(user.getUser(), ModerationType.ACCEPTED, page);
                postCount = postRepository.getPostsModeratedByUserCount(user.getUser(), ModerationType.ACCEPTED);
                break;
            default:
                throw new InternalException("Unknown status", HttpStatus.BAD_REQUEST);
        }
        response.setCount(postCount);
        response.setPosts(posts.stream().map(p -> modelMapper.map(p, PostAnnounceResponse.class)).collect(Collectors.toList()));
        return response;
    }

    private List<PostAnnounceResponse> convertToPostAnnounceList(List<Post> posts){
        return posts.stream().map(post -> modelMapper.map(post, PostAnnounceResponse.class)).collect(Collectors.toList());
    }
}
