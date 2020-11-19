package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CommentListResponse;
import com.example.projects.blogengine.api.response.PostAnnounceResponse;
import com.example.projects.blogengine.api.response.PostListResponse;
import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.PostComment;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Optional;


@Service
public class PostResponseService {

    private final Logger logger = LoggerFactory.getLogger(PostResponseService.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ModelMapper mapper;

    public PostListResponse getPostList(int limit, int offset, String mode){
        //todo assert not null (offset)
        List<Post> posts;
        PostListResponse response = new PostListResponse();
        Pageable page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (mode) {
            case "popular":
                posts = postRepository.getPopularPosts(page);
                break;
            case "best":
                posts = postRepository.getBestPosts(page);
                break;
            case "early":
                posts = postRepository.getEarlyPosts(page);
                break;
            default:
                posts = postRepository.getRecentPosts(page);
                break;
        }
        response.setCount(postRepository.getPostsCount());
        response.setPosts(convertToPostResponse(posts));
        return response;
    }

    private List<PostAnnounceResponse> convertToPostResponse(List<Post> posts){
        List<PostAnnounceResponse> response = new ArrayList<>();
        for (Post post : posts) {
            PostAnnounceResponse postAnnounce = mapper.map(post, PostAnnounceResponse.class);
            postAnnounce.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
            postAnnounce.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
            postAnnounce.setCommentCount(post.getComments().size());
            postAnnounce.setAnnounce(Jsoup.parse(post.getText()).text());
            postAnnounce.setTimestamp(post.getTime().toEpochSecond());
            response.add(postAnnounce);
        }
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
        response.setPosts(convertToPostResponse(postRepository.getPostsByTag(tag, page)));
        return response;
    }

    public PostResponse getPostById(int id) {
        Optional<Post> optionalPost = postRepository.getPostById(id);
        if (optionalPost.isEmpty()){
            return null;
        }
        Post post = optionalPost.get();
        PostResponse response = mapper.map(post, PostResponse.class);
        response.setLikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).count());
        response.setDislikeCount((int) post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).count());
        response.setTimestamp(post.getTime().toEpochSecond());
        List<CommentListResponse> commentList = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (PostComment postComment : post.getComments()) {
            commentList.add(mapper.map(postComment, CommentListResponse.class));
            //todo photo null
        }
        for (Tag tag : post.getTags()) {
            tags.add(tag.getName());
        }
        response.setComments(commentList);
        response.setTags(tags);
        return  response;
    }

    public PostListResponse getPostListToModeration(int limit, int offset, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        ModerationType type = ModerationType.valueOf(status.toUpperCase());
        List<Post> postsModeratedByUser = postRepository.getPostsModeratedByUser(user.getUser(), type, page);
        response.setCount(postRepository.getPostsModeratedByUserCount(user.getUser(), type));
        response.setPosts(convertToPostResponse(postsModeratedByUser));
        return response;
    }
}
