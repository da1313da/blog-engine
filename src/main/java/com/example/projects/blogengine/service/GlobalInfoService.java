package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.GeneralInfoResponse;
import com.example.projects.blogengine.config.BlogProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalInfoService {
    @Autowired
    private BlogProperties properties;
    @Autowired
    private ModelMapper modelMapper;

    public GeneralInfoResponse getGlobalInfo(){
        return modelMapper.map(properties.getGeneralInfo(), GeneralInfoResponse.class);
    }

}
