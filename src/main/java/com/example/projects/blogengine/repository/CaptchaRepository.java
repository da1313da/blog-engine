package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.CaptchaCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

    Optional<CaptchaCode> getBySecretCode(String secretCode);

}
