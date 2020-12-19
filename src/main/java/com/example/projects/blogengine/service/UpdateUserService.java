package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.CaptchaCode;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.CaptchaRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.utility.TokenGenerator;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateUserService {

    private final Logger logger = LoggerFactory.getLogger(UpdateUserService.class);

    private final PasswordEncoder passwordEncoder;
    private final CaptchaRepository captchaRepository;
    private final TokenGenerator tokenGenerator;
    private final UserRepository userRepository;
    private final BlogProperties blogProperties;
    private final SpringEmailService springEmailService;

    public GenericResponse restoreUserPassword(EmailRequest email) {
        Optional<User> userByEmail = userRepository.getUserByEmail(email.getEmail());
        GenericResponse response = new GenericResponse();
        if (userByEmail.isPresent()){
            User user = userByEmail.get();
            String code = tokenGenerator.getToken(blogProperties.getAccount().getPasswordRestoreTokenLength());
            user.setCode(code);
            userRepository.save(user);
            String authority = blogProperties.getHostPath();
            String link = "/login/change-password/" + code;
            springEmailService.sendSimpleMessage(email.getEmail(), "Сброс пароля", "Для сброса пароля перейдите по ссылке " + authority + link);
            response.setResult(true);
        } else {
            response.setResult(false);
        }
        return response;
    }

    public GenericResponse changeUserPassword(ChangePasswordRequest request) {
        Optional<User> optionalUser = userRepository.getByCode(request.getCode());
        Map<String, String> errors = new HashMap<>();
        GenericResponse response = new GenericResponse();
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            CaptchaCode captchaCode = captchaRepository.getBySecretCode(request.getCaptchaSecret())
                    .orElseThrow(() -> new NotFoundException("Captcha secret code is outdated!", HttpStatus.BAD_REQUEST));
            if (!captchaCode.getCode().equals(request.getCaptcha())){
                errors.put("captcha", "Код с картинки введён неверно");
            }
            if (request.getPassword().length() < blogProperties.getAccount().getPasswordLength()){
                errors.put("password", "Пароль короче 6-ти символов");
            }
            if (errors.size() > 0){
                response.setErrors(errors);
                response.setResult(false);
            } else {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                response.setResult(true);
            }
        } else {
            errors.put("code", "Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\"/auth/restore\">Запросить ссылку снова</a>");
            response.setResult(false);
        }
        return response;
    }
}
