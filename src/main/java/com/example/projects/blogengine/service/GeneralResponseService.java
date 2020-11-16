package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.TagWeightResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.model.Tag;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@Service
public class GeneralResponseService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    public TagsListResponse getTagList(String query) {
        TagsListResponse response = new TagsListResponse();
        List<Tag> tags;
        List<TagWeightResponse> tagWeightResponses = new ArrayList<>();
        if (query == null || query.equals("")){
            tags = tagRepository.getTags();
        } else {
            tags = tagRepository.getTagStartsWith(query);
        }
        OptionalInt max = tags.stream().mapToInt(tag -> tag.getPosts().size()).max();
        if (max.isEmpty()){
            return response;
        }
        if (max.getAsInt() == 0) return response;
        tags.forEach(tag -> tagWeightResponses.add(new TagWeightResponse(tag.getName(), (double) tag.getPosts().size() / max.getAsInt())));
        response.setTags(tagWeightResponses);
        return response;
    }
}

