package com.example.projects.blogengine.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface UserForLoginResponse {

    Integer getId();

    String getName();

    String getPhoto();

    String getEmail();

    @JsonIgnore
    Byte getDbModeration();

    default Boolean getModeration(){
        return getDbModeration() == 1;
    }

    Integer getModerationCount();

    default Boolean getSettings(){
        return getDbModeration() == 1;
    }
}
