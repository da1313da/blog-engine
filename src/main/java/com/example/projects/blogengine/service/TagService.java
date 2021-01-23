package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.TagWeightResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.repository.TagRepository;
import com.example.projects.blogengine.repository.projections.TagWithPostCount;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public TagsListResponse getTagList(String query) {
        TagsListResponse response = new TagsListResponse();
        if (query == null || query.isEmpty()){
            List<TagWithPostCount> tagsWithPostCount = tagRepository.getTagsWithPostCount();
            int max = tagsWithPostCount.stream()
                    .map(TagWithPostCount::getPostCount).max(Integer::compareTo).orElse(0);
            if (max != 0){
                List<TagWeightResponse> weightResponses = tagsWithPostCount.stream()
                        .map(e -> new TagWeightResponse(e.getTagName(), ((double) e.getPostCount()) / max))
                        .collect(Collectors.toList());
                response.setTags(weightResponses);
            }
        } else {
            List<TagWithPostCount> tagsWithPostCount = tagRepository.getTagsWithPostCountBySearchQuery(query);
            int max = tagsWithPostCount.stream()
                    .map(TagWithPostCount::getPostCount).max(Integer::compareTo).orElse(0);
            if (max != 0){
                List<TagWeightResponse> weightResponses = tagRepository.getTagsWithPostCount().stream()
                        .map(e -> new TagWeightResponse(e.getTagName(), ((double) e.getPostCount()) / max))
                        .collect(Collectors.toList());
                response.setTags(weightResponses);
            }
        }
        return response;
    }
}

