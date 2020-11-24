package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.CommentRequest;
import com.example.projects.blogengine.api.request.CreatePostRequest;
import com.example.projects.blogengine.api.request.LikeRequest;
import com.example.projects.blogengine.api.request.ModerationRequest;
import com.example.projects.blogengine.api.response.CreatePostResponse;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.PostListResponse;
import com.example.projects.blogengine.api.response.PostResponse;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.PostResponseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {

    private final Logger logger = LoggerFactory.getLogger(ApiPostController.class);

    @Autowired
    private PostResponseService responseService;

    @GetMapping("api/post")
    public PostListResponse getPostResponse(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "limit") int limit,
                                            @RequestParam(name = "mode") String mode){
        return responseService.getPostList(limit, offset, mode);
    }

    @GetMapping("api/post/byDate")
    public PostListResponse getPostResponseByDate(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                            @RequestParam(name = "limit") int limit,
                                            @RequestParam(name = "date") String date){
        return responseService.getPostListByDate(limit, offset, date);
    }

    @GetMapping("api/post/byTag")
    public PostListResponse getPostResponseByTag(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                  @RequestParam(name = "limit") int limit,
                                                  @RequestParam(name = "tag") String tag){
        return responseService.getPostListByTag(limit, offset, tag);
    }

    @GetMapping("api/post/search")
    public PostListResponse getPostsByQuery(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "query") String query){
        return responseService.getPostsByQuery(limit, offset, query);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @GetMapping("api/post/moderation")
    public PostListResponse getModeratorPosts(@RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                                 @RequestParam(name = "limit") int limit,
                                                 @RequestParam(name = "status") String status,
                                                        @AuthenticationPrincipal UserDetailsImpl user){

        return responseService.getPostListToModeration(limit, offset, status, user);
    }

    @GetMapping("api/post/{id}")
    public ResponseEntity<PostResponse> getPostResponseByTag(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl user){
        PostResponse response = responseService.getPostById(id, user);
        if (response == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/post/my")
    public PostListResponse getUserPosts(@RequestParam int offset,
                                         @RequestParam int limit,
                                         @RequestParam String status,
                                         @AuthenticationPrincipal UserDetailsImpl user){
        return responseService.getUserPosts(offset, limit, status, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post")
    public CreatePostResponse createPost(@RequestBody CreatePostRequest request, @AuthenticationPrincipal UserDetailsImpl user){
        return responseService.createPost(request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("api/post/{id}")
    public CreatePostResponse updatePost(@PathVariable int id,
                                                   @RequestBody CreatePostRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl user){
        return responseService.updatePost(id, request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/comment")
    public Object addComment(@RequestBody CommentRequest request, @AuthenticationPrincipal UserDetailsImpl user){
        return responseService.addComment(request, user);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PostMapping("/api/moderation")
    public GenericResponse moderatePost(@RequestBody ModerationRequest request,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails){
        return responseService.moderatePost(request, userDetails);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/like")
    public GenericResponse addLike(@RequestBody LikeRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return responseService.addLike(request, userDetails, true);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("/api/post/dislike")
    public GenericResponse addDislike(@RequestBody LikeRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return responseService.addLike(request, userDetails, false);
    }
}
