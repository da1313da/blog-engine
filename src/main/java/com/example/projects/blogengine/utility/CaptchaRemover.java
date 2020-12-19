package com.example.projects.blogengine.utility;

import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.repository.CaptchaRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CaptchaRemover {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaRemover.class);

    private final CaptchaRepository captchaRepository;
    private final BlogProperties blogProperties;

    @Scheduled(fixedDelayString = "#{blogProperties.getCaptcha().getDeleteTime().toMillis()}")
    public void remove(){
        captchaRepository.deleteAll();
    }

}
