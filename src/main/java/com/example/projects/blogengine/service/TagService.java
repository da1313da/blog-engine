package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.TagWeightResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.projections.TagWithPostCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public TagsListResponse getTagList(String query) {
        TagsListResponse response = new TagsListResponse();
        if (query == null || query.isEmpty()){
            int max = tagRepository.getTagsWithPostCount().stream()
                    .map(TagWithPostCount::getPostCount).max(Integer::compareTo).orElse(0);
            if (max != 0){
                List<TagWeightResponse> weightResponses = tagRepository.getTagsWithPostCount().stream()
                        .map(e -> new TagWeightResponse(e.getTag().getName(), ((double) e.getPostCount()) / max))
                        .collect(Collectors.toList());
                response.setTags(weightResponses);
            }
        } else {
            int max = tagRepository.getTagsWithPostCountBySearchQuery(query).stream()
                    .map(TagWithPostCount::getPostCount).max(Integer::compareTo).orElse(0);
            if (max != 0){
                List<TagWeightResponse> weightResponses = tagRepository.getTagsWithPostCount().stream()
                        .map(e -> new TagWeightResponse(e.getTag().getName(), ((double) e.getPostCount()) / max))
                        .collect(Collectors.toList());
                response.setTags(weightResponses);
            }
        }
        return response;
    }
}

