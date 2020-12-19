package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.CommentRequest;
import com.example.projects.blogengine.api.request.CreatePostRequest;
import com.example.projects.blogengine.api.request.LikeRequest;
import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.PostListResponse;
import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.PostAttributesService;
import com.example.projects.blogengine.service.PostCollectionSrvice;
import com.example.projects.blogengine.service.PostResponseService;
import com.example.projects.blogengine.service.PostUpdateService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ApiPostController {

    private final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    private final PostResponseService postResponseService;
    private final PostCollectionSrvice postCollectionSrvice;
    private final PostUpdateService postUpdateService;
    private final PostAttributesService postAttributesService;

    @GetMapping("api/post")
    public PostListResponse getPostResponse(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "limit", required = false, defaultValue = "1") int limit,
                                            @RequestParam(name = "mode", required = false, defaultValue = "recent") String mode){
        return postCollectionSrvice.getPostList(limit, offset, mode);
    }

    @GetMapping("api/post/byDate")
    public PostListResponse getPostResponseByDate(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "limit", required = false, defaultValue = "1") int limit,
                                            @RequestParam(name = "date", required = false, defaultValue = "#{T(java.time.ZonedDateTime).now(T(java.time.ZoneId).of(\"UTC\")).format(T(java.time.format.DateTimeFormatter).ofPattern(\"yyyy-MM-dd\"))}") String date){
        System.out.println(date);
        return postCollectionSrvice.getPostListByDate(limit, offset, date);
    }

    @GetMapping("api/post/byTag")
    public PostListResponse getPostResponseByTag(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit", required = false, defaultValue = "1") int limit,
                                                 @RequestParam(name = "tag") String tag){
        return postCollectionSrvice.getPostListByTag(limit, offset, tag);
    }

    @GetMapping("api/post/search")
    public PostListResponse getPostsByQuery(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "query") String query){
        return postCollectionSrvice.getPostsByQuery(limit, offset, query);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("api/post/moderation")
    public PostListResponse getModeratorPosts(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "status") String status,
                                                        @AuthenticationPrincipal UserDetailsImpl user){

        return postCollectionSrvice.getPostListToModeration(limit, offset, status, user);
    }

    @GetMapping("api/post/{id}")
    public PostResponse getPostResponseByTag(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl user){
        return postResponseService.getPostById(id, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/post/my")
    public PostListResponse getUserPosts(@RequestParam int offset,
                                         @RequestParam int limit,
                                         @RequestParam String status,
                                         @AuthenticationPrincipal UserDetailsImpl user){
        return postCollectionSrvice.getUserPosts(offset, limit, status, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post")
    public GenericResponse createPost(@RequestBody CreatePostRequest request, @AuthenticationPrincipal UserDetailsImpl user){
        return postUpdateService.createPost(request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("api/post/{id}")
    public GenericResponse updatePost(@PathVariable int id,
                                                   @RequestBody CreatePostRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user){
        return postUpdateService.updatePost(id, request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/comment")
    public Object addComment(@RequestBody CommentRequest request, @AuthenticationPrincipal UserDetailsImpl user){
        return postAttributesService.addComment(request, user);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/api/moderation")
    public GenericResponse moderatePost(@RequestBody ModerationRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postUpdateService.moderatePost(request, userDetails);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/like")
    public GenericResponse addLike(@RequestBody LikeRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postAttributesService.addLike(request, userDetails, true);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/dislike")
    public GenericResponse addDislike(@RequestBody LikeRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postAttributesService.addLike(request, userDetails, false);
    }
}
