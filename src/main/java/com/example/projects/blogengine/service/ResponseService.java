package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.data.CommentForPostById;
import com.example.projects.blogengine.data.PostForPostByIdResponse;
import com.example.projects.blogengine.model.PostComment;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.CommentRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import com.example.projects.blogengine.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class ResponseService {

    private final Logger logger = LoggerFactory.getLogger(ResponseService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthService authService;

    public PostResponse getPostResponse(int limit, int offset, String mode) {
        PostResponse response = new PostResponse();
        int count = postRepository.getPostsCount(ZonedDateTime.now());
        response.setCount(count);
        if (count == 0){
            response.setPosts(new ArrayList<>());
            return response;
        }
        switch (mode) {
            case "recent":
                PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.by("time").descending());
                response.setPosts(postRepository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "popular":
                page = new PageRequestWithOffset(limit, offset, Sort.by("comments").descending());
                response.setPosts(postRepository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "best":
                page = new PageRequestWithOffset(limit, offset, Sort.by("likes").descending());
                response.setPosts(postRepository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
            case "early":
                page = new PageRequestWithOffset(limit, offset, Sort.by("time").ascending());
                response.setPosts(postRepository.getPostsForPostResponse(ZonedDateTime.now(), page));
                break;
        }
        return response;
    }

    public PostResponse getPostResponseByDate(int limit, int offset, String date){
        PostResponse postResponse = new PostResponse();
        String startDateStr = date + " 00:00:00";
        String endDateStr = date + " 23:59:59";
        //todo dates!!!!
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("Europe/Moscow"));
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.parse(endDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), ZoneId.of("Europe/Moscow"));
        int count = postRepository.getPostsCountByDate(ZonedDateTime.now(), start, end);
        postResponse.setCount(count);
        if (count == 0){
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        postResponse.setPosts(postRepository.getPostsForPostResponseByDate(ZonedDateTime.now(), start, end, page));
        return postResponse;
    }

    public PostResponse getPostResponseByTag(int limit, int offset, String tag) {
        PostResponse postResponse = new PostResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        String now = ZonedDateTime.now().toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
        int count = postRepository.getPostsCountByTag(now, tag);
        postResponse.setCount(count);
        if (count == 0){
            postResponse.setPosts(new ArrayList<>());
            return postResponse;
        }
        postResponse.setPosts(postRepository.getPostsForPostResponseByTag(now, tag, page));
        return postResponse;
    }

    public PostByIdResponse getPostByIdResponse(Integer id, HttpSession session){
        Optional<PostForPostByIdResponse> optional = postRepository.getPostsForPostByIdResponse(id, ZonedDateTime.now());
        if (optional.isEmpty()) return null;
        PostForPostByIdResponse post = optional.get();
        List<CommentForPostById> comments = commentRepository.getByPost(id);
        List<String> tags = tagRepository.getByPost(id);
        PostByIdResponse response = new PostByIdResponse();
        response.setId(post.getId());
        response.setTimestamp(post.getTime().toEpochSecond());
        response.setActive(post.getIsActive() == 1);//todo how we can distinguish between different active states?
        response.setUser(post.getUser());
        response.setTitle(post.getTitle());
        response.setText(post.getText());
        response.setLikeCount(post.getLikes() == null ? 0 : post.getLikes());
        response.setDislikeCount(post.getDislikes() == null ? 0 : post.getDislikes());
        response.setViewCount(post.getViewCount());
        response.setComments(comments);
        response.setTags(tags);
        Integer senderId;
        synchronized (authService.getSessionId()){
            senderId = authService.getSessionId().get(session.getId());
        }
        if (senderId != null && !senderId.equals(post.getUser().getId()) && !senderId.equals(post.getModerator().getId())){
            postRepository.increaseViewCount(id);
        }
        return response;
    }
}
