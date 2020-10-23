package com.example.projects.blogengine.data;

import com.example.projects.blogengine.data.dto.PostsDto;
import com.example.projects.blogengine.data.dto.UserDto;
import com.example.projects.blogengine.model.Users;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Repository
public class PostsDao {
    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> getPosts(int offset, int limit){
        Query<?> query = entityManager.
                unwrap(Session.class).
                createQuery("SELECT p.id," +
                        " p.time," +
                        " p.userId," +
                        " p.title," +
                        " p.text," +
                        " sum(case when pv.value = 1 then 1 else 0 end) / count(distinct pc.id)," +
                        " sum(case when pv.value = -1 then 1 else 0 end) / count(distinct pc.id)," +
                        " count(distinct pc.id)," +
                        " p.viewCount" +
                        " FROM Posts p LEFT JOIN PostComments pc ON p.id = pc.postId LEFT JOIN PostVotes pv ON p.id = pv.postId group by p.id");
        ScrollableResults scrollableResults = query.scroll(ScrollMode.SCROLL_SENSITIVE);
        List<PostsDto> posts = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        scrollableResults.scroll(offset);
        int i = 0;
        while (scrollableResults.next() && limit > i++){
            posts.add(new PostsDto((Integer) scrollableResults.get(0),
                    ((ZonedDateTime)(scrollableResults.get(1))).toInstant().toEpochMilli(),
                    new UserDto(((Users)(scrollableResults.get(2))).getId(), ((Users)(scrollableResults.get(2))).getName()),
                    (String) scrollableResults.get(3),
                    Jsoup.parse((String) scrollableResults.get(4)).text(),
                    scrollableResults.get(5) != null? (Long) scrollableResults.get(5) : 0,
                    scrollableResults.get(6) != null? (Long) scrollableResults.get(6) : 0,
                    scrollableResults.get(7) != null? (Long) scrollableResults.get(7) : 0,
                    (Integer) scrollableResults.get(8)));
        }
        result.put("count", scrollableResults.last() ? scrollableResults.getRowNumber() + 1 : 0);
        result.put("posts", posts);
        return result;
    }
}
