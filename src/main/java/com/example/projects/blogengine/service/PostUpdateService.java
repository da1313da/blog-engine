package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.CreatePostRequest;
import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.AccessDeniedException;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.*;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostUpdateService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final BlogProperties blogProperties;


    public GenericResponse createPost(CreatePostRequest request, UserDetailsImpl user) {
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = validateCreatePostRequest(request);
        if (errors.size() > 0){
            response.setErrors(errors);
        } else {
            Post post = new Post();
            ModerationType moderationType = getPostModerationTypeOnCreate(user);
            post.setModerationStatus(moderationType);
            ZonedDateTime postTime = getPostTime(request);
            post.setTime(postTime);
            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            User actualUser = userRepository.findById(user.getUser().getId())
                    .orElseThrow(() -> new NotFoundException("User " + user.getUser().getEmail() + " not found!", HttpStatus.BAD_REQUEST));
            post.setUser(actualUser);
            post.setViewCount(0);
            post.setTitle(request.getTitle());
            List<Tag> requestedTags = getRequestedTags(request);
            requestedTags.forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public GenericResponse updatePost(int id, CreatePostRequest request, UserDetailsImpl user) {
        Post post = postRepository.getPostByIdPreloadTags(id)
                .orElseThrow(() -> new NotFoundException("post with id " + id + " not found!", HttpStatus.BAD_REQUEST));
        GenericResponse response = new GenericResponse();
        Map<String, String> errors  = validateCreatePostRequest(request);
        if (errors.size() > 0){
            response.setErrors(errors);
        } else {
            ModerationType moderationType = getPostModerationTypeOnUpdate(user, post);
            post.setModerationStatus(moderationType);
            ZonedDateTime postTime = getPostTime(request);
            post.setTime(postTime);
            post.setIsActive((byte) request.getActive());
            post.setText(request.getText());
            post.setTitle(request.getTitle());
            List<Tag> requestedTags = getRequestedTags(request);
            post.getTags().stream().filter(tag -> !requestedTags.contains(tag)).forEach(post::removeTag);
            requestedTags.stream().filter(tag -> !post.getTags().contains(tag)).forEach(post::addTag);
            postRepository.save(post);
            response.setResult(true);
        }
        return response;
    }

    public GenericResponse moderatePost(ModerationRequest request, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("Post with id " + request.getPostId() + " not found!", HttpStatus.BAD_REQUEST));
        User moderator = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User with id " +userDetails.getUser().getId() + " not found!", HttpStatus.BAD_REQUEST));
        if (moderator.getIsModerator() != 1) {
            throw new AccessDeniedException("User with id doesn't have permission to moderate!", HttpStatus.BAD_REQUEST);
        }
        GenericResponse response = new GenericResponse();
        post.setModerationStatus(request.getDecision().equals("accept") ? ModerationType.ACCEPTED : ModerationType.DECLINED);
        post.setModerator(moderator);
        postRepository.save(post);
        response.setResult(true);
        return response;
    }

    private ZonedDateTime getPostTime(CreatePostRequest request) {
        ZonedDateTime postTime = ZonedDateTime
                .of(LocalDateTime.ofEpochSecond(request.getTimestamp(), 0, ZoneOffset.UTC), ZoneId.of("UTC"));
        if (postTime.compareTo(ZonedDateTime.now(ZoneId.of("UTC"))) < 0) {
            postTime = ZonedDateTime.now(ZoneId.of("UTC"));
        }
        return postTime;
    }

    private Map<String, String> validateCreatePostRequest(CreatePostRequest request) {
        Map<String, String> errors = new HashMap<>();
        if (request.getTitle() == null) {
            errors.put("title", "Заголовок не установлен");
        }
        if (request.getText() == null) {
            errors.put("text", "Текст не установлен");
        }
        if (request.getTitle().length() < blogProperties.getPost().getMinTitleSize()) {
            errors.put("title", "Заголовок публикации слишком короткий");
        }
        if (request.getText().length() < blogProperties.getPost().getMinTextSize()) {
            errors.put("text", "Текст публикации слишком короткий");
        }
        return errors;
    }

    private List<Tag> getRequestedTags(CreatePostRequest request) {
        List<String> tagNames = request.getTags().stream().map(String::toUpperCase).collect(Collectors.toList());
        List<Tag> newTags = tagRepository.getTagsByName(tagNames);
        List<String> existingTagNames = newTags.stream().map(Tag::getName).collect(Collectors.toList());
        for (String tagName : tagNames) {
            if (!existingTagNames.contains(tagName)){
                Tag tag = new Tag();
                tag.setName(tagName.toUpperCase().trim());
                tagRepository.save(tag);
                newTags.add(tag);
            }
        }
        return newTags;
    }

    private ModerationType getPostModerationTypeOnUpdate(UserDetailsImpl user, Post post) {
        GlobalSettings postPreModeration = globalSettingsRepository.getByCode("POST_PREMODERATION")
                .orElseThrow(() -> new NotFoundException("Global statistics not found!", HttpStatus.BAD_REQUEST));
        if (postPreModeration.getValue().equals("YES") && post.getUser().getId().equals(user.getUser().getId())){
            return ModerationType.NEW;
        } else {
            return post.getModerationStatus();
        }
    }

    private ModerationType getPostModerationTypeOnCreate(UserDetailsImpl user) {
        GlobalSettings postPreModeration = globalSettingsRepository.getByCode("POST_PREMODERATION")
                .orElseThrow(() -> new NotFoundException("Global statistics not found!", HttpStatus.BAD_REQUEST));
        if (postPreModeration.getValue().equals("YES") && !user.getUser().getIsModerator().equals((byte)1)){
            return ModerationType.NEW;
        } else {
            return ModerationType.ACCEPTED;
        }
    }

}
