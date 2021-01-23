package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.GeneralInfoResponse;
import com.example.projects.blogengine.config.BlogProperties;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GlobalInfoService {

    private final BlogProperties properties;
    private final ModelMapper modelMapper;

    public GeneralInfoResponse getGlobalInfo(){
        return modelMapper.map(properties.getGeneralInfo(), GeneralInfoResponse.class);
    }

}
