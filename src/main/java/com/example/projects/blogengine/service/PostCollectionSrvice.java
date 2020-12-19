package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.PostAnnounceResponse;
import com.example.projects.blogengine.api.response.PostListResponse;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostCollectionSrvice {

    private final Logger logger = LoggerFactory.getLogger(PostCollectionSrvice.class);

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostListResponse getPostList(int limit, int offset, String mode){
        List<Post> posts;
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (mode) {
            case "popular":
                List<Post> prepare = postRepository.getPopularPosts1(page);
                posts = postRepository.getPopularPosts2(prepare);
                break;
            case "best":
                posts = postRepository.getBestPosts2(postRepository.getBestPosts1(page));
                break;
            case "early":
                posts = postRepository.getEarlyPosts2(postRepository.getEarlyPosts1(page));
                break;
            default:
                posts = postRepository.getRecentPosts2(postRepository.getRecentPosts1(page));
                break;
        }
        response.setCount(postRepository.getPostsCount());
        response.setPosts(convertToPostResponse(posts));
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
            response.setPosts(convertToPostResponse(postsByDate));
        }catch (DateTimeParseException e){
            logger.info(e.toString());
        }
        return response;
    }

    public PostListResponse getPostListByTag(int limit, int offset, String tag) {
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        response.setCount(postRepository.getPostsCountByTag(tag));
        response.setPosts(convertToPostResponse(postRepository.getPostsByTag1(postRepository.getPostsByTag(tag, page))));
        return response;
    }

    public PostListResponse getUserPosts(int offset, int limit, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (status){
            case "inactive":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.NEW, ModerationType.ACCEPTED, ModerationType.DECLINED), (byte) 0, page))));
                response.setCount(postRepository.getUserPostCount(user.getUser(),
                        List.of(ModerationType.NEW, ModerationType.ACCEPTED, ModerationType.DECLINED), (byte) 0));
                break;
            case "pending":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.NEW), (byte) 1, page))));
                response.setCount(postRepository.getUserPostCount(user.getUser(),
                        List.of(ModerationType.NEW), (byte) 1));
                break;
            case "declined":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.DECLINED), (byte) 1, page))));
                response.setCount(postRepository.getUserPostCount(user.getUser(),
                        List.of(ModerationType.DECLINED), (byte) 1));
                break;
            case "published":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.ACCEPTED), (byte) 1, page))));
                response.setCount(postRepository.getUserPostCount(user.getUser(),
                        List.of(ModerationType.ACCEPTED), (byte) 1));
                break;
        }
        return response;
    }

    public PostListResponse getPostsByQuery(int limit, int offset, String query) {
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        PostListResponse response = new PostListResponse();
        response.setCount(postRepository.getPostCountByQuery(query));
        response.setPosts(convertToPostResponse(postRepository.getPostsByQuery(postRepository.getPostsByQuery(query, page))));
        return response;
    }

    public PostListResponse getPostListToModeration(int limit, int offset, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        ModerationType type = ModerationType.valueOf(status.toUpperCase());
        List<Post> postsModeratedByUser = postRepository.getModeratedPosts(user.getUser(), type, page);
        response.setCount(postRepository.getModeratedPostCount(user.getUser(), type));
        response.setPosts(convertToPostResponse(postsModeratedByUser));
        return response;
    }

    private List<PostAnnounceResponse> convertToPostResponse(List<Post> posts){
        List<PostAnnounceResponse> response = new ArrayList<>();
        for (Post post : posts) {
            PostAnnounceResponse postAnnounce = modelMapper.map(post, PostAnnounceResponse.class);
            postAnnounce.setLikeCount(post.getLikes().size());
            postAnnounce.setDislikeCount(post.getDisLikes().size());
            postAnnounce.setCommentCount(post.getComments().size());
            postAnnounce.setAnnounce(Jsoup.parse(post.getText()).text());
            postAnnounce.setTimestamp(post.getTime().toEpochSecond());
            response.add(postAnnounce);
        }
        return response;
    }
}
