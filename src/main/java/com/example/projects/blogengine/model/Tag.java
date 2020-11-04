package com.example.projects.blogengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"posts"})
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public void addPost(Post post){
        posts.add(post);
        post.getTags().add(this);
    }

    public void removePost(Post post){
        posts.remove(post);
        post.getTags().remove(this);
    }
}
