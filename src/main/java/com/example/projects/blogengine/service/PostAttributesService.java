package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.CommentRequest;
import com.example.projects.blogengine.api.request.LikeRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.PostComment;
import com.example.projects.blogengine.model.PostVote;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.CommentRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.repository.VoteRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostAttributesService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final BlogProperties blogProperties;
    private final UserRepository userRepository;

    public Object addComment(CommentRequest request, UserDetailsImpl user) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new NotFoundException("Post with id " + request.getPostId() + " not found!", HttpStatus.BAD_REQUEST));
        PostComment postComment = new PostComment();
        //todo move to controller advise?
        if (request.getText() == null || request.getText().length() < blogProperties.getPost().getMinCommentSize()){
            GenericResponse response = new GenericResponse();
            response.setResult(false);
            Map<String, String> errors = new HashMap<>();
            errors.put("text", "Текст комментария не задан или слишком короткий");
            response.setErrors(errors);
            return response;
        }
        User actualUser = userRepository.findById(user.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User with id " + user.getUser().getId() + " not found!", HttpStatus.BAD_REQUEST));
        if (request.getParenId() != null && !request.getParenId().equals("")){
            //comment on comment
            int parentId;
            try{
                parentId = Integer.parseInt(request.getParenId());
            } catch (NumberFormatException e){
                throw new NotFoundException("Bad comment id " + request.getParenId() + " not found!", HttpStatus.BAD_REQUEST);
            }
            PostComment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new NotFoundException("Comment with id " + parentId + " not found!", HttpStatus.BAD_REQUEST));
            postComment.setParent(parentComment);
        }
        //comment on post
        postComment.setPost(post);
        postComment.setText(request.getText());
        postComment.setUser(actualUser);
        postComment = commentRepository.save(postComment);
        Map<String, Integer> response = new HashMap<>();
        response.put("id", postComment.getId());
        return response;
    }

    public GenericResponse addLike(LikeRequest request, UserDetailsImpl userDetails, boolean isLike) {
        GenericResponse response = new GenericResponse();
        byte sign = (byte) (isLike ? 1 : -1);
        Optional<PostVote> vote = voteRepository.getUserVoteByPost(request.getPostId(), userDetails.getUser().getId());
        if (vote.isEmpty()){
            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(() -> new NotFoundException("Post with id " + request.getPostId() + " not found!", HttpStatus.BAD_REQUEST));
            User user = userRepository.findById(userDetails.getUser().getId())
                    .orElseThrow(() -> new NotFoundException("User with id " +userDetails.getUser().getId() + " not found!", HttpStatus.BAD_REQUEST));
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
            if (existingVote.getValue() != sign){
                existingVote.setValue(sign);
                voteRepository.save(existingVote);
                response.setResult(true);
            } else {
                response.setResult(false);
            }
        }
        return response;
    }

}
