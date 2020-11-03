package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.data.UserForLoginResponse;
import com.example.projects.blogengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UsersRepository extends JpaRepository<User, Integer> {
    @Query("select u.id as id," +
            " u.name as name," +
            " u.photo as photo," +
            " u.email as email," +
            " u.isModerator as dbModeration," +
            " count(u.id) as moderationCount" +
            " from User u join Post p on u.id = p.moderator" +
            " where u.email = ?1 and u.password = ?2 group by (u.id)")
    UserForLoginResponse getUserForLoginResponse(String email, String password);

    @Query("select u.id as id," +
            " u.name as name," +
            " u.photo as photo," +
            " u.email as email," +
            " u.isModerator as dbModeration," +
            " count(u.id) as moderationCount" +
            " from User u join Post p on u.id = p.moderator" +
            " where u.id = ?1")
    UserForLoginResponse getUserForLoginResponseById(Integer id);

    User getUsersByEmailAndPassword(String email, String password);

    User getUsersByEmail(String email);

    User getByCode(String code);
}
