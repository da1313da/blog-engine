package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> getByCode(String code);

    Optional<User> getUserByEmail(String email);

    @Query("select u from User u where  u.id <> ?2 and u.email = ?1")
    Optional<User> getUserByEmailNotEqual(String email, int userId);

}
