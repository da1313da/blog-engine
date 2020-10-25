package com.example.projects.blogengine;

import com.example.projects.blogengine.data.GlobalSettingsRepository;
import com.example.projects.blogengine.model.GlobalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        globalSettingsRepository.save(new GlobalSettings("code", "name", "value"));
    }
}
