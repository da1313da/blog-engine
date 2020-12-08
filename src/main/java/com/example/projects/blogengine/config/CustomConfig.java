package com.example.projects.blogengine.config;

import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.PostComment;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.PostsStatistics;
import org.jsoup.Jsoup;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class CustomConfig {

    @Bean
    ModelMapper getMapper(){
        ModelMapper modelMapper = new ModelMapper();

        Converter<PostsStatistics, StatisticResponse> postsStatisticsStatisticResponseConverter = mappingContext -> {
            PostsStatistics source = mappingContext.getSource();
            StatisticResponse destination = new StatisticResponse();
            destination.setPostsCount(Objects.requireNonNullElse(source.getPostsCount(), 0));
            destination.setLikesCount(Objects.requireNonNullElse(source.getLikesCount(), 0));
            destination.setDislikesCount(Objects.requireNonNullElse(source.getDislikesCount(), 0));
            destination.setViewsCount(Objects.requireNonNullElse(source.getViewsCount(), 0));
            destination.setFirstPublication(Objects.requireNonNullElse(source.getFirstPublication(), 0L));
            return destination;
        };
        TypeMap<PostsStatistics, StatisticResponse> postsStatisticsStatisticResponseTypeMap = modelMapper.createTypeMap(PostsStatistics.class, StatisticResponse.class);
        postsStatisticsStatisticResponseTypeMap.setConverter(postsStatisticsStatisticResponseConverter);

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

        Converter<User, UserPostResponse> userUserPostResponseConverter = mappingContext -> {
            User source = mappingContext.getSource();
            UserPostResponse destination = new UserPostResponse();
            destination.setId(source.getId());
            destination.setName(source.getName());
            return destination;
        };
        TypeMap<User, UserPostResponse> userUserPostResponseTypeMap = modelMapper.createTypeMap(User.class, UserPostResponse.class);
        userUserPostResponseTypeMap.setConverter(userUserPostResponseConverter);

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
            destination.setDislikeCount(source.getDisLikes().size());
            destination.setLikeCount(source.getLikes().size());
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

        Converter<Post, PostAnnounceResponse> postPostAnnounceResponseConverter = mappingContext -> {
            Post source = mappingContext.getSource();
            PostAnnounceResponse destination = new PostAnnounceResponse();
            destination.setUser(modelMapper.map(source.getUser(), UserPostResponse.class));
            destination.setTitle(source.getTitle());
            destination.setId(source.getId());
            destination.setDislikeCount(source.getDisLikes().size());
            destination.setLikeCount(source.getLikes().size());
            destination.setAnnounce(Jsoup.parse(source.getText()).text());
            destination.setCommentCount(source.getComments().size());
            destination.setViewCount(source.getViewCount());
            destination.setTimestamp(source.getTime().toEpochSecond());
            return destination;
        };
        TypeMap<Post, PostAnnounceResponse > postPostAnnounceResponseTypeMap = modelMapper.createTypeMap(Post.class, PostAnnounceResponse.class);
        postPostAnnounceResponseTypeMap.setConverter(postPostAnnounceResponseConverter);

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

}
