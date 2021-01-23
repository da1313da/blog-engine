package com.example.projects.blogengine.service;

import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GlobalSettingsService {

    private final GlobalSettingsRepository repository;

    public Map<String, Boolean> getSettings(){
        List<GlobalSettings> settings = (List<GlobalSettings>) repository.findAll();
        Map<String, Boolean> response = new HashMap<>();
        for (GlobalSettings setting : settings) {
            response.put(setting.getCode(), setting.getValue().equals("YES"));
        }
        return response;
    }

    public void setSettings(Map<String, Boolean> request){
        request.forEach((s, b) -> {
            Optional<GlobalSettings> param = repository.getByCode(s);
            param.ifPresent(globalSettings -> {
                globalSettings.setValue(b ? "YES" : "NO");
                repository.save(globalSettings);
            });
        });
    }
}
