package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.data.CommentForPostById;
import com.example.projects.blogengine.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<PostComment, Integer> {
    @Query("select pc.id as id, pc.time as time,pc.text as text, pc.user as user from PostComment pc where pc.post.id = ?1")
    List<CommentForPostById> getByPost(Integer id);
}
