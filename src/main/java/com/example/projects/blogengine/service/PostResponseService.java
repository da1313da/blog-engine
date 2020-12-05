package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.CommentRequest;
import com.example.projects.blogengine.api.request.CreatePostRequest;
import com.example.projects.blogengine.api.request.LikeRequest;
import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.CommentNotFoundException;
import com.example.projects.blogengine.exception.GlobalSettingsNotFountException;
import com.example.projects.blogengine.exception.PostNotFountException;
import com.example.projects.blogengine.exception.UserNotFoundException;
import com.example.projects.blogengine.model.*;
import com.example.projects.blogengine.repository.*;
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
import java.util.stream.Collectors;


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
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private BlogProperties blogProperties;

    public PostListResponse getPostList(int limit, int offset, String mode){
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

    public GenericResponse createPost(CreatePostRequest request, UserDetailsImpl user) {
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = new HashMap<>();
        if (request.getTitle() == null) {
            errors.put("title", "Заголовок не установлен");
        }
        if(request.getText() == null){
            errors.put("text", "Текст не установлен");
        }
        if (request.getTitle().length() <= blogProperties.getPost().getMinTitleSize()){
            errors.put("title", "Заголовок публикации слишком короткий");
        }
        if (request.getText().length() <= blogProperties.getPost().getMinTextSize()){
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (errors.size() > 0){
            response.setErrors(errors);
        } else {
            Post post = new Post();
            Optional<GlobalSettings> param = globalSettingsRepository.getByCode("POST_PREMODERATION");
            GlobalSettings postPreModeration;
            if (param.isPresent()){
                postPreModeration = param.get();
            } else {
                throw new GlobalSettingsNotFountException();
            }
            if (postPreModeration.getValue().equals("YES")){
                post.setModerationStatus(ModerationType.NEW);
            } else {
                post.setModerationStatus(ModerationType.ACCEPTED);
            }
            ZonedDateTime postTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC), ZoneId.of("UTC"));
            if (postTime.compareTo(ZonedDateTime.now(ZoneId.of("UTC"))) < 0) {
                postTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
            post.setTime(postTime);

            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            User actualUser = userRepository.findById(user.getUser().getId())
                    .orElseThrow(() -> new UsernameNotFoundException(user.getUser().getEmail() + " not found"));
            post.setUser(actualUser);
            post.setViewCount(0);
            post.setTitle(request.getTitle());

            List<Tag> newTags = tagRepository.getTagsByName(request.getTags());
            List<String> existingTagNames = newTags.stream().map(Tag::getName).collect(Collectors.toList());
            for (String tagName : request.getTags()) {
                if (!existingTagNames.contains(tagName)){
                    Tag tag = new Tag();
                    tag.setName(tagName.toUpperCase().trim());
                    tagRepository.save(tag);
                    newTags.add(tag);
                }
            }
            newTags.forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public GenericResponse updatePost(int id, CreatePostRequest request, UserDetailsImpl user) {
        Optional<Post> optionalPost = postRepository.getPostByIdPreloadTags(id);
        if (optionalPost.isEmpty()){
            return null;
        }
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = new HashMap<>();
        if (request.getTitle() == null) {
            errors.put("title", "Заголовок не установлен");
        }
        if(request.getText() == null){
            errors.put("text", "Текст не установлен");
        }
        if (request.getTitle().length() <= blogProperties.getPost().getMinTitleSize()){
            errors.put("title", "Заголовок публикации слишком короткий");
        }
        if (request.getText().length() <= blogProperties.getPost().getMinTextSize()){
            errors.put("text", "Текст публикации слишком короткий");
        }
        if (errors.size() > 0){
            response.setErrors(errors);
        } else {
            Post post = optionalPost.get();
            Optional<GlobalSettings> param = globalSettingsRepository.getByCode("POST_PREMODERATION");
            GlobalSettings postPreModeration;
            if (param.isPresent()){
                postPreModeration = param.get();
            } else {
                throw new GlobalSettingsNotFountException();
            }
            if (postPreModeration.getValue().equals("YES") && post.getUser().getId().equals(user.getUser().getId())){
                post.setModerationStatus(ModerationType.NEW);
            }
            ZonedDateTime postTime = ZonedDateTime.of(LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC), ZoneId.of("UTC"));
            if (postTime.compareTo(ZonedDateTime.now(ZoneId.of("UTC"))) < 0) {
                postTime = ZonedDateTime.now(ZoneId.of("UTC"));
            }
            post.setTime(postTime);
            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            post.setTitle(request.getTitle());
            List<Tag> newTags = tagRepository.getTagsByName(request.getTags());
            List<String> existingTagNames = newTags.stream().map(Tag::getName).collect(Collectors.toList());
            for (String tagName : request.getTags()) {
                if (!existingTagNames.contains(tagName)){
                    Tag tag = new Tag();
                    tag.setName(tagName.toUpperCase().trim());
                    tagRepository.save(tag);
                    newTags.add(tag);
                }
            }
            post.getTags().stream().filter(tag -> !newTags.contains(tag)).forEach(post::removeTag);
            newTags.stream().filter(tag -> !post.getTags().contains(tag)).forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public Object addComment(CommentRequest request, UserDetailsImpl user) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow(PostNotFountException::new);
        PostComment postComment = new PostComment();
        if (request.getParenId() != null && !request.getParenId().equals("")){
            //comment on comment
            int parentId = Integer.parseInt(request.getParenId());//todo parse exception!!
            PostComment parentComment = commentRepository.findById(parentId).orElseThrow(CommentNotFoundException::new);
            postComment.setParent(parentComment);
        }
        //comment on post
        if (request.getText() == null || request.getText().length() < blogProperties.getPost().getMinCommentSize()){
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
        post.setModerationStatus(request.getDecision().equals("accept") ? ModerationType.ACCEPTED : ModerationType.DECLINED);
        postRepository.save(post);
        response.setResult(true);
        return response;
    }

    public GenericResponse addLike(LikeRequest request, UserDetailsImpl userDetails, boolean isLike) {
        GenericResponse response = new GenericResponse();
        byte sign = (byte) (isLike ? 1 : -1);
        Optional<PostVote> vote = voteRepository.getUserVoteByPost(request.getPostId(), userDetails.getUser().getId());
        if (vote.isEmpty()){
            Post post = postRepository.findById(request.getPostId()).orElseThrow(PostNotFountException::new);
            User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(UserNotFoundException::new);
            if (post.getUser().getId().equals(user.getId())){
                response.setResult(false);
                return response;
            }
            PostVote newVote = new PostVote();
            newVote.setValue(sign);
            newVote.setPost(post);
            newVote.setUser(user);
            voteRepository.save(newVote);
            response.setResult(true);
        } else {
            PostVote existingVote = vote.get();
            if (existingVote.getValue() == -sign){
                existingVote.setValue(sign);
                voteRepository.save(existingVote);
                response.setResult(true);
            } else {
                response.setResult(false);
            }
        }
        return response;
    }

    public PostListResponse getPostsByQuery(int limit, int offset, String query) {
        PageRequestWithOffset page = new PageRequestWithOffset(limit, offset, Sort.unsorted());
        PostListResponse response = new PostListResponse();
        response.setCount(postRepository.getPostCountByQuery(query));
        response.setPosts(convertToPostResponse(postRepository.getPostsByQuery(query, page)));
        return response;
    }
}
