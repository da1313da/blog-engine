package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.GlobalSettings;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer> {
    Optional<GlobalSettings> getByCode(String code);
}
