package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.model.CaptchaCode;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.CaptchaRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.utility.TokenGenerator;
import com.github.cage.GCage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private CaptchaRepository captchaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SpringEmailService emailService;

    @Transactional
    public CaptchaResponse getCaptchaResponse() {//todo move hardcoded values to props?
        captchaRepository.deleteCaptchaCodes();
        GCage cage = new GCage();
        CaptchaResponse response = new CaptchaResponse();
        String secret = TokenGenerator.getToken(20);
        String code = TokenGenerator.getToken(5);
        BufferedImage baseImage = cage.drawImage(code);
        BufferedImage sizedImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        sizedImage.createGraphics().drawImage(baseImage, 0, 0, 100, 35, null);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ImageIO.write(sizedImage, "png", buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] byteArray = buffer.toByteArray();
        String encodedImage = Base64.getEncoder().encodeToString(byteArray);
        String image = "data:image/png;base64, " + encodedImage;
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(code);
        captchaCode.setSecretCode(secret);
        captchaRepository.save(captchaCode);
        response.setImage(image);
        response.setSecret(secret);
        return response;
    }

    public RegistrationResponse getRegistrationResponse(RegistrationRequest registrationRequest) {
        CaptchaCode captcha = captchaRepository.getBySecretCode(registrationRequest.getCaptchaSecret()).orElseThrow(IllegalArgumentException::new);//todo new exe?
        RegistrationErrorsResponse errors = new RegistrationErrorsResponse();
        RegistrationResponse response = new RegistrationResponse();
        boolean isErrorPresent = false;
        if (usersRepository.getUserByEmail(registrationRequest.getEmail()).isPresent()){
            errors.setEmail("Этот e-mail уже зарегистрирован");
            isErrorPresent = true;
        }
        if(registrationRequest.getPassword().length() < 6){
            errors.setPassword("Пароль короче 6-ти символов");
            isErrorPresent = true;
        }
        if (!registrationRequest.getCaptcha().equals(captcha.getCode())){
            errors.setCaptcha("Код с картинки введён неверно");
            isErrorPresent = true;
        }
        if (registrationRequest.getName().contains("?")){
            //todo any name conditions?
            isErrorPresent = true;
        }
        if (isErrorPresent){
            response.setResult(false);
            response.setErrors(errors);
        } else {
            User user = new User();
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setName(registrationRequest.getName());
            user.setEmail(registrationRequest.getEmail());
            user.setIsModerator((byte) 0);//todo
            usersRepository.save(user);
            response.setResult(true);
        }
        return response;
    }

    public BooleanResponse getRestoreResult(EmailRequest email) {
        User user = usersRepository.getUserByEmailExpl(email.getEmail());
        BooleanResponse response = new BooleanResponse();
        if (user != null){
            String code = TokenGenerator.getToken(30);
            user.setCode(code);
            usersRepository.save(user);
            String authority = "http://localhost:8080";//todo move to props?
            String link = "/login/change-password/" + code;
            //emailService.sendSimpleMessage(email.getEmail(), "Сброс пароля", "Для сброса пароля перейдите по ссылке " + authority + link);
            response.setResult(true);
        } else {
            response.setResult(false);
        }
        return response;
    }

    public ChangePasswordResponse getChangePasswordRequest(ChangePasswordRequest changePasswordData) {
        User users = usersRepository.getByCode(changePasswordData.getCode());
        ChangePasswordErrorsResponse errors = new ChangePasswordErrorsResponse();
        ChangePasswordResponse response = new ChangePasswordResponse();
        if (users != null){
            CaptchaCode captchaCode = captchaRepository.getBySecretCode(changePasswordData.getCaptchaSecret()).orElseThrow(IllegalArgumentException::new);
            boolean isErrorPresent = false;
            if (!captchaCode.getCode().equals(changePasswordData.getCaptcha())){
                errors.setCaptcha("Код с картинки введён неверно");
                isErrorPresent = true;
            }
            if (changePasswordData.getPassword().length() < 6){//todo hardcoded
                errors.setPassword("Пароль короче 6-ти символов");
                isErrorPresent = true;
            }
            if (isErrorPresent){
                response.setErrors(errors);
                response.setResult(false);
            } else {
                users.setPassword(passwordEncoder.encode(changePasswordData.getPassword()));
                usersRepository.save(users);
                response.setResult(true);
            }
        } else {
            errors.setCode("Ссылка для восстановления пароля устарела.\n" +
                    "\t\t\t\t<a href=\n" +
                    "\t\t\t\t\\\"/auth/restore\\\">Запросить ссылку снова</a>");
            response.setResult(false);
            response.setErrors(errors);
        }
        return response;
    }
}
