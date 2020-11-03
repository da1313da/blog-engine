package com.example.projects.blogengine.repository;

import com.example.projects.blogengine.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {
    @Modifying
    @Query(value = "delete from captcha_codes cc where date_add(cc.time, interval 1 hour) < ?1 and cc.id > 0", nativeQuery = true)
    void deleteCaptchaCodes(String time);

    Optional<CaptchaCode> getBySecretCode(String secretCode);
}
