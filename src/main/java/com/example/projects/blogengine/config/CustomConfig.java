package com.example.projects.blogengine.config;

import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.PostComment;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.PostWithStatistics;
import org.jsoup.Jsoup;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class CustomConfig {

    @Bean
    ModelMapper getMapper(){
        ModelMapper modelMapper = new ModelMapper();
        addPostCommentToCommentListResponseMapping(modelMapper);
        addUserToUserPostResponseMapping(modelMapper);
        addPostToPostResponseMapping(modelMapper);//depends on addPostCommentToCommentListResponseMapping
        addPostToPostAnnounceResponseMapping(modelMapper);//depends on addUserToUserPostResponseMapping
        addPostWithStatisticsToPostAnnounceResponseMapping(modelMapper);//depends on addUserToUserPostResponseMapping
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    private void addPostWithStatisticsToPostAnnounceResponseMapping(ModelMapper modelMapper) {
        Converter<PostWithStatistics, PostAnnounceResponse> converter = mappingContext -> {
            PostWithStatistics source = mappingContext.getSource();
            PostAnnounceResponse destination = new PostAnnounceResponse();
            destination.setUser(modelMapper.map(source.getPost().getUser(), UserPostResponse.class));
            destination.setTitle(source.getPost().getTitle());
            destination.setId(source.getPost().getId());
            destination.setDislikeCount(source.getDislikes());
            destination.setLikeCount(source.getLikes());
            destination.setAnnounce(Jsoup.parse(source.getPost().getText()).text());
            destination.setCommentCount(source.getCommentCount());
            destination.setViewCount(source.getPost().getViewCount());
            destination.setTimestamp(source.getPost().getTime().toEpochSecond());
            return destination;
        };
        TypeMap<PostWithStatistics, PostAnnounceResponse> postsStatisticsStatisticResponseTypeMap =
                modelMapper.createTypeMap(PostWithStatistics.class, PostAnnounceResponse.class);
        postsStatisticsStatisticResponseTypeMap.setConverter(converter);
    }

    private void addPostCommentToCommentListResponseMapping(ModelMapper modelMapper) {
        Converter<PostComment, CommentListResponse> postCommentCommentListResponseConverter = mappingContext -> {
            PostComment source = mappingContext.getSource();
            CommentListResponse destination = new CommentListResponse();
            UserCommentResponse userCommentResponse = new UserCommentResponse();
            userCommentResponse.setId(source.getUser().getId());
            userCommentResponse.setName(source.getUser().getName());
            userCommentResponse.setPhoto(source.getUser().getPhoto());
            destination.setId(source.getId());
            destination.setText(source.getText());
            destination.setTimestamp(source.getTime().toEpochSecond());
            destination.setUser(userCommentResponse);
            return destination;
        };
        TypeMap<PostComment, CommentListResponse> postCommentCommentListResponseTypeMap = modelMapper.createTypeMap(PostComment.class, CommentListResponse.class);
        postCommentCommentListResponseTypeMap.setConverter(postCommentCommentListResponseConverter);
    }

    private void addUserToUserPostResponseMapping(ModelMapper modelMapper) {
        Converter<User, UserPostResponse> userUserPostResponseConverter = mappingContext -> {
            User source = mappingContext.getSource();
            UserPostResponse destination = new UserPostResponse();
            destination.setId(source.getId());
            destination.setName(source.getName());
            return destination;
        };
        TypeMap<User, UserPostResponse> userUserPostResponseTypeMap = modelMapper.createTypeMap(User.class, UserPostResponse.class);
        userUserPostResponseTypeMap.setConverter(userUserPostResponseConverter);
    }

    private void addPostToPostResponseMapping(ModelMapper modelMapper) {
        Converter<Post, PostResponse>  postPostResponseConverter = mappingContext -> {
            Post source = mappingContext.getSource();
            PostResponse destination = new PostResponse();
            UserPostResponse userPostResponse = new UserPostResponse();
            userPostResponse.setId(source.getUser().getId());
            userPostResponse.setName(source.getUser().getName());
            destination.setTags(source.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
            destination.setComments(source.getComments()
                    .stream().map(postComment -> modelMapper.map(postComment, CommentListResponse.class)).collect(Collectors.toList()));
            destination.setActive(source.getIsActive() == 1);
            destination.setDislikeCount((int) source.getVotes().stream().filter(v -> v.getValue() == -1).count());
            destination.setLikeCount((int) source.getVotes().stream().filter(v -> v.getValue() == 1).count());
            destination.setId(source.getId());
            destination.setText(source.getText());
            destination.setTitle(source.getTitle());
            destination.setViewCount(source.getViewCount());
            destination.setTimestamp(source.getTime().toEpochSecond());
            destination.setUser(userPostResponse);
            return destination;
        };
        TypeMap<Post, PostResponse> postPostResponseTypeMap = modelMapper.createTypeMap(Post.class, PostResponse.class);
        postPostResponseTypeMap.setConverter(postPostResponseConverter);
    }

    private void addPostToPostAnnounceResponseMapping(ModelMapper modelMapper) {
        Converter<Post, PostAnnounceResponse> postPostAnnounceResponseConverter = mappingContext -> {
            Post source = mappingContext.getSource();
            PostAnnounceResponse destination = new PostAnnounceResponse();
            destination.setUser(modelMapper.map(source.getUser(), UserPostResponse.class));
            destination.setTitle(source.getTitle());
            destination.setId(source.getId());
            destination.setDislikeCount((int) source.getVotes().stream().filter(v -> v.getValue() == -1).count());
            destination.setLikeCount((int) source.getVotes().stream().filter(v -> v.getValue() == 1).count());
            destination.setAnnounce(Jsoup.parse(source.getText()).text());
            destination.setCommentCount(source.getComments().size());
            destination.setViewCount(source.getViewCount());
            destination.setTimestamp(source.getTime().toEpochSecond());
            return destination;
        };
        TypeMap<Post, PostAnnounceResponse > postPostAnnounceResponseTypeMap = modelMapper.createTypeMap(Post.class, PostAnnounceResponse.class);
        postPostAnnounceResponseTypeMap.setConverter(postPostAnnounceResponseConverter);
    }

}
