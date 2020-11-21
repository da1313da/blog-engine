package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.CommentRequest;
import com.example.projects.blogengine.api.request.CreatePostRequest;
import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.exception.CommentNotFoundException;
import com.example.projects.blogengine.exception.PostNotFountException;
import com.example.projects.blogengine.exception.UserNotFoundException;
import com.example.projects.blogengine.model.*;
import com.example.projects.blogengine.repository.CommentRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.utility.PageRequestWithOffset;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


@Service
public class PostResponseService {

    private final Logger logger = LoggerFactory.getLogger(PostResponseService.class);
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
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

    public PostResponse getPostById(int id, UserDetailsImpl user) {
        Optional<Post> optionalPost = postRepository.getPostById(id);
        if (optionalPost.isEmpty()){
            return null;
        }
        Post post = optionalPost.get();
        if (user != null && user.getUser().getIsModerator() != 1 && !post.getUser().getId().equals(user.getUser().getId())){
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
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
        List<Post> postsModeratedByUser = postRepository.getModeratedPosts(user.getUser(), type, page);
        response.setCount(postRepository.getModeratedPostCount(user.getUser(), type));
        response.setPosts(convertToPostResponse(postsModeratedByUser));
        return response;
    }

    public PostListResponse getUserPosts(int offset, int limit, String status, UserDetailsImpl user) {
        PostListResponse response = new PostListResponse();
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        switch (status){
            case "inactive":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.NEW, ModerationType.ACCEPTED, ModerationType.DECLINED), (byte) 0, page)));
                response.setCount(postRepository.getUserPostsCount(user.getUser(),
                        List.of(ModerationType.NEW, ModerationType.ACCEPTED, ModerationType.DECLINED), (byte) 0));
                break;
            case "pending":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.NEW), (byte) 1, page)));
                response.setCount(postRepository.getUserPostsCount(user.getUser(),
                        List.of(ModerationType.NEW), (byte) 1));
                break;
            case "declined":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.DECLINED), (byte) 1, page)));
                response.setCount(postRepository.getUserPostsCount(user.getUser(),
                        List.of(ModerationType.DECLINED), (byte) 1));
                break;
            case "published":
                response.setPosts(convertToPostResponse(postRepository.getUserPosts(user.getUser(),
                        List.of(ModerationType.ACCEPTED), (byte) 1, page)));
                response.setCount(postRepository.getUserPostsCount(user.getUser(),
                        List.of(ModerationType.ACCEPTED), (byte) 1));
                break;
        }
        return response;
    }

    public CreatePostResponse createPost(CreatePostRequest request, UserDetailsImpl user) {
        Post post = new Post();
        CreatePostResponse response = new CreatePostResponse();
        CreatePostErrorsResponse errors = new CreatePostErrorsResponse();
        boolean isError = false;
        if (request.getTitle() == null) {
            errors.setTitle("Заголовок не установлен");
            isError = true;
        } else if(request.getText() == null){
            errors.setText("Текст не установлен");
            isError = true;
        } else if (request.getTitle().length() <= 3){
            errors.setTitle("Заголовок публикации слишком короткий");
            isError = true;
        } else if (request.getText().length() <= 50){
            errors.setText("Текст публикации слишком короткий");
            isError = true;
        }
        List<Tag> tags = tagRepository.getTagsByName(request.getTags());
        if (tags.isEmpty()) isError = true;
        if (isError){
            response.setErrors(errors);
        } else {
            ZonedDateTime postTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC), ZoneId.of("UTC"));
            if (postTime.compareTo(ZonedDateTime.now(ZoneId.of("UTC"))) < 0) {
                postTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            User actualUser = userRepository.findById(user.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException(user.getUser().getEmail() + " not found"));
            post.setUser(actualUser);
            //todo how to select moderator ?
            List<User> moderators =userRepository.getModerators();
            if (moderators.isEmpty()) {
                //?
            }
            post.setTitle(request.getTitle());
            post.setModerator(moderators.get(0));
            post.setModerationStatus(ModerationType.NEW);
            post.setTime(postTime);
            post.setViewCount(0);
            tags.forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public CreatePostResponse updatePost(int id, CreatePostRequest request, UserDetailsImpl user) {
        Optional<Post> optionalPost = postRepository.getPostByIdPreloadTags(id);
        if (optionalPost.isEmpty()){
            return null;
        }
        CreatePostResponse response = new CreatePostResponse();
        CreatePostErrorsResponse errors = new CreatePostErrorsResponse();
        boolean isError = false;
        if (request.getTitle() == null) {
            errors.setTitle("Заголовок не установлен");
            isError = true;
        } else if(request.getText() == null){
            errors.setText("Текст не установлен");
            isError = true;
        } else if (request.getTitle().length() <= 3){
            errors.setTitle("Заголовок публикации слишком короткий");
            isError = true;
        } else if (request.getText().length() <= 50){
            errors.setText("Текст публикации слишком короткий");
            isError = true;
        }
        List<Tag> tags = tagRepository.getTagsByName(request.getTags());
        if (tags.isEmpty()) isError = true;
        if (isError){
            response.setErrors(errors);
        } else {
            Post post = optionalPost.get();
            ZonedDateTime postTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC), ZoneId.of("UTC"));
            if (postTime.compareTo(ZonedDateTime.now(ZoneId.of("UTC"))) < 0) {
                postTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            if (post.getUser().getId().equals(user.getUser().getId())){
                post.setModerationStatus(ModerationType.NEW);
            }
            post.setTitle(request.getTitle());
            post.setTime(postTime);
            post.getTags().stream().filter(tag -> !tags.contains(tag)).forEach(post::removeTag);
            tags.forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public Object addComment(CommentRequest request, UserDetailsImpl user) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow(PostNotFountException::new);
        PostComment postComment = new PostComment();
        if (!request.getParenId().equals("")){
            //comment on comment
            int parentId = Integer.parseInt(request.getParenId());
            PostComment parentComment = commentRepository.findById(parentId).orElseThrow(CommentNotFoundException::new);
            postComment.setParent(parentComment);
        }
        //comment on post
        if (request.getText() == null || request.getText().length() < 30){
            GenericResponse response = new GenericResponse();
            response.setResult(false);
            Map<String, String> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            response.setErrors(errors);
            return response;
        }
        User actualUser = userRepository.findById(user.getUser().getId()).orElseThrow(UserNotFoundException::new);
        postComment.setPost(post);
        postComment.setText(request.getText());
        postComment.setUser(actualUser);
        postComment = commentRepository.save(postComment);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", postComment.getId());
        return response;
    }

    public GenericResponse moderatePost(ModerationRequest request, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow(PostNotFountException::new);
        User moderator = userRepository.findById(userDetails.getUser().getId()).orElseThrow(UserNotFoundException::new);
        if (moderator.getIsModerator() != 1) throw new UserNotFoundException();
        GenericResponse response = new GenericResponse();
        post.setModerationStatus(ModerationType.valueOf(request.getDecision().toUpperCase()));
        postRepository.save(post);
        response.setResult(true);
        return response;
    }
}
