package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<PostVote, Integer> {
    @Query("select pv from PostVote pv where pv.post.id = ?1 and pv.user.id = ?2")
    Optional<PostVote> getUserVoteByPost(int postId, int userId);
}
