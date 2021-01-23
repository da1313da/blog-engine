package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.projections.TagWithPostCount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Class TagRepository")
class TagRepositoryTest {

    public static final String TAG_SEARCH_NAME = "T1";
    public static final List<String> TAG_NAME_LIST = List.of("T1", "T2");
    @Autowired
    private TagRepository tagRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void getTagsByName() {
        List<Tag> expected = entityManager.createNativeQuery("select t.* from tags t where t.name in :list", Tag.class)
                .setParameter("list", TAG_NAME_LIST)
                .getResultList();
        entityManager.clear();
        List<Tag> actual = tagRepository.getTagsByName(TAG_NAME_LIST);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnTagsWithPostCount() {
        List<Object[]> expected = entityManager.createNativeQuery("select t.name as tagName," +
                " (select count(tp.post_id) from tag2post tp where tp.tag_id = t.id) as postCount from tags t").getResultList();
        entityManager.clear();
        List<TagWithPostCount> actual = tagRepository.getTagsWithPostCount();
        Assertions.assertThat(actual)
                .extracting(TagWithPostCount::getTagName)
                .containsExactlyElementsOf(expected.stream().map(a -> (String) a[0]).collect(Collectors.toList()));
        Assertions.assertThat(actual)
                .extracting(TagWithPostCount::getPostCount)
                .containsExactlyElementsOf(expected.stream().map(a -> ((BigInteger) a[1]).intValue()).collect(Collectors.toList()));
    }

    @SuppressWarnings({"unchecked"})
    @Transactional
    @Test
    void shouldReturnTagsWithPostCountByQuery() {
        List<Object[]> expected = entityManager.createNativeQuery("select t.name as tagName," +
                " (select count(tp.post_id) from tag2post tp where tp.tag_id = t.id) as postCount from tags t where t.name like :word")
                .setParameter("word", TAG_SEARCH_NAME + "%")
                .getResultList();
        entityManager.clear();
        List<TagWithPostCount> actual = tagRepository.getTagsWithPostCountBySearchQuery(TAG_SEARCH_NAME);
        Assertions.assertThat(actual)
                .extracting(TagWithPostCount::getTagName)
                .containsExactlyElementsOf(expected.stream().map(a -> (String) a[0]).collect(Collectors.toList()));
        Assertions.assertThat(actual)
                .extracting(TagWithPostCount::getPostCount)
                .containsExactlyElementsOf(expected.stream().map(a -> ((BigInteger) a[1]).intValue()).collect(Collectors.toList()));
    }
}