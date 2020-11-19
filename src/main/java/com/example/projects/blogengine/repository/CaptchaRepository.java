package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

    @Modifying
    @Query("delete from CaptchaCode c where c.time + 3600 < now()")
    void deleteCaptchaCodes();

    Optional<CaptchaCode> getBySecretCode(String secretCode);
}
