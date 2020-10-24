package com.example.projects.blogengine.model;

import org.apache.catalina.User;
import org.aspectj.apache.bcel.classfile.Module;
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
    public void addUsersAndPosts(){
        List<Users> usersList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Users u = new Users();
            u.setCode("secret code");
            u.setEmail("u" + i + "@mail.ru");
            u.setIsModerator((byte) 1);
            u.setName("user" + i);
            u.setPassword("pwd" + i);
            u.setPhoto("u" + i + "/p" + i);
            u.setRegTime(ZonedDateTime.now().plusMinutes(new Random().nextInt(10)));
            entityManager.persist(u);
            usersList.add(u);
        }
        for (int i = 0; i < 10; i++) {
            Posts post = new Posts();
            Users user = usersList.get(new Random().nextInt(3));
            post.setIsActive((byte) new Random().nextInt(1));
            post.setModerationStatus(getRandomModerationStatus());
            post.setText("i am user " + user.getId());
            post.setTime(user.getRegTime().plusMinutes(new Random().nextInt(10)));
            post.setTitle("title");
            post.setViewCount(0);

            post.setModeratorId(usersList.get(0));
            post.setUserId(usersList.get(new Random().nextInt(3)));

//            usersList.get(0).getModerationPosts().add(post);
//            user.getRegularPosts().add(post);
//            entityManager.persist(usersList.get(0));
//            entityManager.persist(user);
            entityManager.persist(post);
        }
    }

    @Test
    @Transactional
    public void usersToPostsBidirectionalTest(){
        List<Users> usersList = entityManager.createQuery("select u from Users u", Users.class).getResultList();
        usersList.forEach(users -> users.getRegularPosts().forEach(posts -> System.out.println(users.getId() + " - " + posts.getId())));
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void addTagsTest(){
        for (int i = 0; i < 30; i++) {
            Tags tag = new Tags();
            tag.setName("tag " + i);
            entityManager.persist(tag);
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void addTagToPostsLinksTest(){
        List<Posts> postsList = entityManager.createQuery("select p from Posts p", Posts.class).getResultList();
        List<Tags> tagsList = entityManager.createQuery("select t from Tags t", Tags.class).getResultList();
        for (int i = 0; i < 60; i++) {
           Tags randomTag = tagsList.get(new Random().nextInt(tagsList.size()));
           Posts randomPost = postsList.get(new Random().nextInt(postsList.size()));
           TagToPost tagToPost = new TagToPost();
           tagToPost.setTagId(randomTag);
           tagToPost.setPostId(randomPost);
           entityManager.persist(tagToPost);
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void tagToPostBidirectionalTest(){
        List<Tags> tagsList = entityManager.createQuery("select t from Tags t", Tags.class).getResultList();
        tagsList.forEach(tags -> tags.getLinkedPosts().
                forEach(link -> System.out.println(tags.getId() + " - (" + link.getTagId().getId() + ", " + link.getPostId().getId() + ")")));
        List<Posts> postsList = entityManager.createQuery("select p from Posts p", Posts.class).getResultList();
        postsList.forEach(posts -> posts.getLinkedTags().forEach(tagToPost -> System.out.println(posts.getId() + " - (" + tagToPost.getPostId().getId() + ", " + tagToPost.getTagId())));
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void cascadeTest(){
        //todo
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void addPostComments(){
        List<Posts> postsList = entityManager.createQuery("select p from Posts p", Posts.class).getResultList();
        List<Users> usersList = entityManager.createQuery("select u from Users u", Users.class).getResultList();
        for (Posts post : postsList) {
            for (int i = 0; i < new Random().nextInt(10); i++) {
                PostComments comment = new PostComments();
                Users user = usersList.get(new Random().nextInt(usersList.size()));
                comment.setPostId(post);
                comment.setText("user" + user.getId() + " comments");
                comment.setTime(post.getTime().plusMinutes(new Random().nextInt(10)));
                comment.setUserId(user);
                post.getComments().add(comment);
            }
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void addVotes(){
        List<Posts> postsList = entityManager.createQuery("select p from Posts p", Posts.class).getResultList();
        List<Users> usersList = entityManager.createQuery("select u from Users u", Users.class).getResultList();
        for (Posts post : postsList) {
            for (Users user : usersList) {
                if (new Random().nextInt(2) == 0) continue;
                PostVotes vote = new PostVotes();
                vote.setPostId(post);
                vote.setTime(post.getTime().plusMinutes(new Random().nextInt(10)));
                vote.setUserId(user);
                vote.setValue((byte) (new Random().nextInt(2) == 0 ? 1 : -1));
                user.getVotes().add(vote);
            }
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
