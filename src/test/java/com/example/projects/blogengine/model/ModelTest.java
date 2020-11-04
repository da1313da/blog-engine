package com.example.projects.blogengine.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class ModelTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    @Rollback(value = false)
    public void generateDb(){
        List<User> usersList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            User u = new User();
            u.setEmail("u" + i + "@mail.ru");
            u.setIsModerator((byte) 1);
            u.setName("user" + i);
            u.setPassword("pwd" + i);
            u.setPhoto("u" + i + "/p" + i);
            u.setRegTime(ZonedDateTime.now().plusMinutes(new Random().nextInt(10)));
            entityManager.persist(u);
            usersList.add(u);
        }
        for (int i = 0; i < 3; i++) {
            User u = new User();
            u.setEmail("u" + i + "@mail.ru");
            u.setIsModerator((byte) 0);
            u.setName("user" + i);
            u.setPassword("pwd" + i);
            u.setPhoto("u" + i + "/p" + i);
            u.setRegTime(ZonedDateTime.now().plusMinutes(new Random().nextInt(10)));
            entityManager.persist(u);
            usersList.add(u);
        }
        List<Post> postsList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Post post = new Post();
            User user = usersList.get(new Random().nextInt(3));
            post.setIsActive((byte) new Random().nextInt(2));
            post.setModerationStatus(getRandomModerationStatus());
            post.setText("i am user " + user.getId());
            post.setTime(user.getRegTime().plusMinutes(new Random().nextInt(10)));
            post.setTitle("title");
            post.setViewCount(0);
            post.setModerator(usersList.get(0));
            post.setUser(usersList.get(new Random().nextInt(3)));
            entityManager.persist(post);
            postsList.add(post);
        }
        for (Post post : postsList) {
            for (int i = 0; i < new Random().nextInt(10); i++) {
                PostComment comment = new PostComment();
                User user = usersList.get(new Random().nextInt(usersList.size()));
                comment.setPost(post);
                comment.setText("user" + user.getId() + " comments");
                comment.setTime(post.getTime().plusMinutes(new Random().nextInt(10)));
                comment.setUser(user);
                post.getComments().add(comment);
            }
        }
        for (Post post : postsList) {
            for (User user : usersList) {
                if (new Random().nextInt(2) == 0) continue;
                PostVote vote = new PostVote();
                vote.setPost(post);
                vote.setTime(post.getTime().plusMinutes(new Random().nextInt(10)));
                vote.setUser(user);
                vote.setValue((byte) (new Random().nextInt(2) == 0 ? 1 : -1));
                user.getVotes().add(vote);
            }
        }
        List<Tag> tagList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Tag tag = new Tag();
            tag.setName("tag " + i);
            entityManager.persist(tag);
            tagList.add(tag);
        }
        for (Post post : postsList) {
            post.addTag(tagList.get(new Random().nextInt(tagList.size())));
        }
    }

    private ModerationType getRandomModerationStatus(){
        int roll = new Random().nextInt(3);
        ModerationType moderationType = null;
        switch (roll){
            case 0:
                moderationType =  ModerationType.ACCEPTED;
                break;
            case 1:
                moderationType = ModerationType.DECLINED;
                break;
            case 2:
                moderationType  = ModerationType.NEW;
        }
        return moderationType;
    }
}
