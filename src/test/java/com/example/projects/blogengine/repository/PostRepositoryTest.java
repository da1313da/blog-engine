package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.Post;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.projections.PostWithStatistics;
import org.assertj.core.api.Assertions;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Class PostRepository")
class PostRepositoryTest {

    public static final int PAGE_SIZE = 10;
    public static final int PAGE_NUMBER = 0;
    public static final int POST_LIST_WITH_SUB_SELECT = 3;
    public static final String SEARCH_WORD_IN_TEXT = "%find%";
    public static final int USER_ID = 1;
    public static final int GET_USER_POST_QUERY_COUNT = 1;
    public static final byte IS_ACTIVE = (byte) 1;


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnRecentPosts(){
        List<Post> expected = entityManager.createNativeQuery(
                "select " +
                "p.* " +
                "from " +
                "posts p " +
                "where " +
                "p.is_active = 1 " +
                "and p.moderation_status = 'ACCEPTED' " +
                "and p.`time` < now() " +
                "order by " +
                "p.`time` desc", Post.class)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        entityManager.clear();
        List<Post> actual = postRepository.getRecentPosts(page);
        //trigger comments and votes load
        actual.forEach(p -> p.getVotes().size());
        actual.forEach(p -> p.getComments().size());
        Assertions.assertThat(actual)
                .hasSize(expected.size())
                .extracting(Post::getId)
                .containsExactlyElementsOf(expected.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(POST_LIST_WITH_SUB_SELECT);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnEarlyPosts(){
        List<Post> expected = entityManager.createNativeQuery(
                "select " +
                "p.* " +
                "from " +
                "posts p " +
                "where " +
                "p.is_active = 1 " +
                "and p.moderation_status = 'ACCEPTED' " +
                "and p.`time` < now() " +
                "order by " +
                "p.`time` asc", Post.class)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        entityManager.clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Post> actual = postRepository.getEarlyPosts(page);
        actual.forEach(p -> p.getVotes().size());
        actual.forEach(p -> p.getComments().size());
        Assertions.assertThat(actual)
                .hasSize(expected.size())
                .extracting(Post::getId)
                .containsExactlyElementsOf(expected.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(POST_LIST_WITH_SUB_SELECT);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnPopularPosts(){
        List<Post> expected = entityManager.createNativeQuery(
                "select " +
                "p.* " +
                "from " +
                "posts p " +
                "where " +
                "p.is_active = 1 " +
                "and p.moderation_status = 'ACCEPTED' " +
                "and p.`time` < now() " +
                "order by " +
                "(select " +
                "count(pc.id) " +
                "from " +
                "post_comments pc " +
                "where " +
                "pc.post_id = p.id) desc", Post.class)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        List<Post> expectedWithComments = expected.stream().filter(p -> p.getComments().size() > 0).collect(Collectors.toList());
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        entityManager.clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Post> actual = postRepository.getPopularPosts(page);
        List<Post> actualWithComments = actual.stream().filter(p -> p.getComments().size() > 0).collect(Collectors.toList());
        actual.forEach(p -> p.getVotes().size());
        actual.forEach(p -> p.getComments().size());
        Assertions.assertThat(actual)
                .hasSize(expected.size())
                .extracting(Post::getId)
                .containsExactlyInAnyOrderElementsOf(expected.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(actualWithComments)
                .extracting(Post::getId)
                .containsExactlyInAnyOrderElementsOf(expectedWithComments.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(POST_LIST_WITH_SUB_SELECT);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnBestPosts(){
        List<Post> expected = entityManager.createNativeQuery(
                "select " +
                    "p.* " +
                    "from " +
                    "posts p " +
                    "where " +
                    "p.is_active = 1 " +
                    "and p.moderation_status = 'ACCEPTED' " +
                    "and p.`time` < now() " +
                    "order by " +
                    "(select " +
                    "count(pv.id) " +
                    "from " +
                    "post_votes pv " +
                    "where " +
                    "pv.post_id = p.id " +
                    "and pv.value = 1) desc", Post.class)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        List<Post> expectedWithLikes = expected.stream()
                .filter(p -> p.getVotes().stream().anyMatch(v -> v.getValue() == 1)).collect(Collectors.toList());
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        entityManager.clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Post> actual = postRepository.getBestPost(page);
        List<Post> actualWithLikes = actual.stream()
                .filter(p -> p.getVotes().stream().anyMatch(v -> v.getValue() == 1)).collect(Collectors.toList());
        actual.forEach(p -> p.getVotes().size());
        actual.forEach(p -> p.getComments().size());
        Assertions.assertThat(actual)
                .hasSize(expected.size())
                .extracting(Post::getId)
                .containsExactlyInAnyOrderElementsOf(expected.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(actualWithLikes)
                .extracting(Post::getId)
                .containsExactlyInAnyOrderElementsOf(expectedWithLikes.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(POST_LIST_WITH_SUB_SELECT);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnPostListContainSearchWordInTextOrTitle() {
        List<Post> expected = entityManager.createNativeQuery(
                "select " +
                "p.* " +
                "from " +
                "posts p " +
                "where " +
                "p.is_active = 1 " +
                "and p.moderation_status = 'ACCEPTED' " +
                "and p.`time` < now() " +
                "and p.title like :word " +
                "or p.`text` like :word", Post.class)
                .setParameter("word", SEARCH_WORD_IN_TEXT)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        entityManager.clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<Post> actual = postRepository.getPostListBySearchWord(SEARCH_WORD_IN_TEXT, page);
        actual.forEach(p -> p.getVotes().size());
        actual.forEach(p -> p.getComments().size());
        Assertions.assertThat(actual)
                .hasSize(expected.size())
                .extracting(Post::getId)
                .containsExactlyElementsOf(expected.stream().map(Post::getId).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(POST_LIST_WITH_SUB_SELECT);
    }

    @Transactional
    @Test
    void shouldReturnPostListCountContainSearchWordInTextOrTitle() {
        BigInteger expected = (BigInteger) entityManager.createNativeQuery(
                "select " +
                    "count(p.id) " +
                    "from " +
                    "posts p " +
                    "where " +
                    "p.is_active = 1 " +
                    "and p.moderation_status = 'ACCEPTED' " +
                    "and p.`time` < now() " +
                    "and p.title like :word " +
                    "or p.`text` like :word")
                .setParameter("word", SEARCH_WORD_IN_TEXT)
                .getSingleResult();
        int actual = postRepository.getPostListCountBySearchWord(SEARCH_WORD_IN_TEXT);
        Assertions.assertThat(actual).isEqualTo(expected.intValue());
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnAcceptedAndActiveUserPost() {
        List<Object[]> expected = entityManager.createNativeQuery("select p.id, " +
                "(select count(pc.id) from post_comments pc where pc.post_id = p.id) as commentCount, " +
                "(select count(pv.id) from post_votes pv where pv.post_id = p.id and pv.value = 1) as likes, " +
                "(select count(pv.id) from post_votes pv where pv.post_id = p.id and pv.value = -1) as dislikes " +
                "from posts p where p.user_id  = :userId and p.is_active = :isActive and p.moderation_status in :moderationStatus and p.`time` < now()")
                .setParameter("userId", USER_ID)
                .setParameter("moderationStatus", List.of(ModerationType.ACCEPTED.toString()))
                .setParameter("isActive", IS_ACTIVE)
                .setFirstResult(PAGE_NUMBER * PAGE_SIZE)
                .setMaxResults(PAGE_SIZE)
                .getResultList();
        User user = entityManager.find(User.class, USER_ID);
        SessionFactory sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
        entityManager.clear();
        Pageable page = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        List<PostWithStatistics> actual = postRepository.getUserPosts(user, List.of(ModerationType.ACCEPTED), (byte) 1, page);
        Assertions.assertThat(actual).hasSize(expected.size())
                .extracting(PostWithStatistics::getPost).extracting(Post::getId)
                .containsExactlyElementsOf(expected.stream().map(a -> (int)a[0]).collect(Collectors.toList()));
        Assertions.assertThat(actual)
                .extracting(PostWithStatistics::getCommentCount)
                .containsExactlyElementsOf(expected.stream().map(a -> ((BigInteger)a[1]).intValue()).collect(Collectors.toList()));
        Assertions.assertThat(actual)
                .extracting(PostWithStatistics::getLikes)
                .containsExactlyElementsOf(expected.stream().map(a -> ((BigInteger)a[2]).intValue()).collect(Collectors.toList()));
        Assertions.assertThat(actual)
                .extracting(PostWithStatistics::getDislikes)
                .containsExactlyElementsOf(expected.stream().map(a -> ((BigInteger)a[3]).intValue()).collect(Collectors.toList()));
        Assertions.assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(GET_USER_POST_QUERY_COUNT);
    }

    @Transactional
    @Test
    void shouldReturnPostCount() {
        BigInteger singleResult = (BigInteger) entityManager.createNativeQuery("select count(p.id) from posts p" +
                " where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.`time` < now()").getSingleResult();
        int expected = singleResult.intValue();
        int actual = postRepository.getPostCount();
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}