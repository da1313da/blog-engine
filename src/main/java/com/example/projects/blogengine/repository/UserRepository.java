package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select count(u) from User u join Post p on u.id = p.moderator where p.moderationStatus = 'NEW' and u = ?1")
    Optional<Integer> getModeratedPostsCount(User user);

    User getUserByEmailAndPassword(String email, String password);

    @Query("select u from User u where u.email = ?1")
    User getUserByEmailExpl(String email);

    User getByCode(String code);

    Optional<User> getUserByEmail(String email);
}
