package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<PostComment, Integer> {

}
