package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.TagWeightResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.projections.TagStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public TagsListResponse getTagList(String query) {
        TagsListResponse response = new TagsListResponse();
        List<TagWeightResponse> weights = new ArrayList<>();
        List<TagStatistics> tagStatistics;
        if (query == null || query.isEmpty()){
            tagStatistics = tagRepository.getTagStatistics();
        } else {
            tagStatistics = tagRepository.getTagStatisticsByTagName(query);
        }
        for (TagStatistics tmp : tagStatistics) {
            weights.add(new TagWeightResponse(tmp.getTagName(), tmp.getTagNorm() < 0.1 ? 0.3 : tmp.getTagNorm()));
        }
        response.setTags(weights);
        return response;
    }
}

