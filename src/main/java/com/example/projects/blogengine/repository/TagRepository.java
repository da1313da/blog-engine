package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.projections.TagStatistics;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @EntityGraph(attributePaths = {"posts.user"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select t from Tag t")
    List<Tag> getTags();

    @Query("select t from Tag t where t.name in ?1")
    List<Tag> getTagsByName(List<String> names);

    @Query(value = "SELECT \n" +
            "    t.name AS tagName,\n" +
            "    CAST(COUNT(ttp.tag_id) AS DOUBLE precision) / (SELECT \n" +
            "            MAX(t_count)\n" +
            "        FROM\n" +
            "            (SELECT \n" +
            "                COUNT(ttp1.tag_id) AS t_count\n" +
            "            FROM\n" +
            "                tags t1\n" +
            "            JOIN tag2post ttp1 ON t1.id = ttp1.tag_id\n" +
            "            GROUP BY ttp1.tag_id) AS tag_max) AS tagNorm\n" +
            "FROM\n" +
            "    tag2post ttp\n" +
            "        JOIN\n" +
            "    tags t ON ttp.tag_id = t.id\n" +
            "        JOIN\n" +
            "    posts p ON ttp.post_id = p.id\n" +
            "WHERE\n" +
            "    p.is_active = 1\n" +
            "        AND p.moderation_status = 'ACCEPTED'\n" +
            "        AND p.time < NOW(0)\n" +
            "        AND t.name LIKE '%1'\n" +
            "GROUP BY ttp.tag_id", nativeQuery = true)
    List<TagStatistics> getTagStatisticsByTagName(String query);

    @Query(value = "SELECT \n" +
            "    t.name AS tagName,\n" +
            "    CAST(COUNT(ttp.tag_id) AS DOUBLE precision) / (SELECT \n" +
            "            MAX(t_count)\n" +
            "        FROM\n" +
            "            (SELECT \n" +
            "                COUNT(ttp1.tag_id) AS t_count\n" +
            "            FROM\n" +
            "                tags t1\n" +
            "            JOIN tag2post ttp1 ON t1.id = ttp1.tag_id\n" +
            "            GROUP BY ttp1.tag_id) AS tag_max) AS tagNorm\n" +
            "FROM\n" +
            "    tag2post ttp\n" +
            "        JOIN\n" +
            "    tags t ON ttp.tag_id = t.id\n" +
            "        JOIN\n" +
            "    posts p ON ttp.post_id = p.id\n" +
            "WHERE\n" +
            "    p.is_active = 1\n" +
            "        AND p.moderation_status = 'ACCEPTED'\n" +
            "        AND p.time < NOW(0)\n" +
            "GROUP BY ttp.tag_id", nativeQuery = true)
    List<TagStatistics> getTagStatistics();


}
