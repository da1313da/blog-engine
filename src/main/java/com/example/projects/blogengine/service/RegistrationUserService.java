package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.AccessDeniedException;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.CaptchaCode;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.CaptchaRepository;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class RegistrationUserService {

    private final GlobalSettingsRepository globalSettingsRepository;
    private final CaptchaRepository captchaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BlogProperties blogProperties;

    public GenericResponse registerNewUser(RegistrationRequest request) {
        GlobalSettings multiUserParam = globalSettingsRepository.getByCode("MULTIUSER_MODE")
                .orElseThrow(() -> new NotFoundException("Global settings not found! Registration is closed by default!", HttpStatus.BAD_REQUEST));
        CaptchaCode captcha = captchaRepository.getBySecretCode(request.getCaptchaSecret())
                .orElseThrow(() -> new NotFoundException("Captcha secret code is outdated!", HttpStatus.BAD_REQUEST));
        GenericResponse response = new GenericResponse();
        if (multiUserParam.getValue().equals("NO")){
            throw new AccessDeniedException("Registration is closed!", HttpStatus.NOT_FOUND);
        }
        Map<String, String> errors = validateRegistrationRequest(request, captcha);
        if (errors.size() > 0){
            response.setResult(false);
            response.setErrors(errors);
        } else {
            User user = new User();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setIsModerator((byte) 0);
            userRepository.save(user);
            response.setResult(true);
        }
        return response;
    }


    private Map<String, String> validateRegistrationRequest(RegistrationRequest request, CaptchaCode captcha) {
        Map<String, String> errors = new HashMap<>();
        if (userRepository.getUserByEmail(request.getEmail()).isPresent()){
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if(request.getPassword().length() < blogProperties.getAccount().getPasswordLength()){
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (!request.getCaptcha().equals(captcha.getCode())){
            errors.put("captcha", "Код с картинки введён неверно");
        }
        if (request.getName().matches("\\W")){
            errors.put("name", "Имя указано неверно");
        }
        return errors;
    }

}
