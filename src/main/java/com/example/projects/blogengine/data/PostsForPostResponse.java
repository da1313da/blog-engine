package com.example.projects.blogengine.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.Jsoup;

import java.time.ZonedDateTime;

public interface PostsForPostResponse {

    Integer getId();

    @JsonIgnore
    ZonedDateTime getTime();

    ShortUser getUser();

    String getTitle();

    @JsonIgnore
    String getText();

    @JsonIgnore
    Integer getLikes();

    @JsonIgnore
    Integer getDislikes();

    @JsonIgnore
    Integer getComments();

    Integer getViewCount();

    default Long getTimestamp(){
        return getTime().toInstant().toEpochMilli();
    }

    default Integer getLikeCount(){
        return getLikes() == null? 0 : getLikes();
    }

    default Integer getDislikeCount(){
        return getDislikes() == null? 0 : getDislikes();
    }

    default Integer getCommentCount(){
        return getComments() == null? 0 : getComments();
    }

    default String getAnnounce(){
        return Jsoup.parse(getText()).text();
    }
}
