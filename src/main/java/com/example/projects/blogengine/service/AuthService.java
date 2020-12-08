package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.ChangePasswordRequest;
import com.example.projects.blogengine.api.request.EmailRequest;
import com.example.projects.blogengine.api.request.LoginRequest;
import com.example.projects.blogengine.api.request.RegistrationRequest;
import com.example.projects.blogengine.api.response.CaptchaResponse;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.LoginResponse;
import com.example.projects.blogengine.api.response.UserLoginResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.GlobalSettingsNotFountException;
import com.example.projects.blogengine.model.CaptchaCode;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.model.ModerationType;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.CaptchaRepository;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.repository.PostRepository;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.utility.TokenGenerator;
import com.github.cage.GCage;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SpringEmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BlogProperties properties;

    @Transactional
    public CaptchaResponse captcha() {
        captchaRepository.deleteCaptchaCodes();
        GCage cage = new GCage();
        CaptchaResponse response = new CaptchaResponse();
        String secret = TokenGenerator.getToken(properties.getCaptcha().getSecretCodeLength());
        String code = TokenGenerator.getToken(properties.getCaptcha().getDisplayCodeLength());
        BufferedImage baseImage = cage.drawImage(code);
        BufferedImage sizedImage = new BufferedImage(
                properties.getCaptcha().getCaptchaImageWidth(),
                properties.getCaptcha().getCaptchaImageHeight(),
                BufferedImage.TYPE_INT_RGB);
        sizedImage.createGraphics().drawImage(
                baseImage,
                0,
                0,
                properties.getCaptcha().getCaptchaImageWidth(),
                properties.getCaptcha().getCaptchaImageHeight(),
                null);
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

    public GenericResponse registration(RegistrationRequest request) {
        Optional<GlobalSettings> multiUserParam = globalSettingsRepository.getByCode("MULTIUSER_MODE");
        if (multiUserParam.isPresent()){
            GlobalSettings param = multiUserParam.get();
            if (param.getValue().equals("NO")){
                return null;
            }
        } else {
            throw new GlobalSettingsNotFountException();
        }
        CaptchaCode captcha = captchaRepository.getBySecretCode(request.getCaptchaSecret()).orElseThrow(IllegalArgumentException::new);
        Map<String, String> errors = new HashMap<>();
        GenericResponse response = new GenericResponse();
        if (userRepository.getUserByEmail(request.getEmail()).isPresent()){
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if(request.getPassword().length() < properties.getAccount().getPasswordLength()){
            errors.put("password", "Пароль короче 6-ти символов");
        }
        if (!request.getCaptcha().equals(captcha.getCode())){
            errors.put("captcha", "Код с картинки введён неверно");
        }
        if (request.getName().matches("\\W")){
            errors.put("name", "Имя указано неверно");
        }
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

    public GenericResponse restorePassword(EmailRequest email) {
        User user = userRepository.getUserByEmailExpl(email.getEmail());
        GenericResponse response = new GenericResponse();
        if (user != null){
            String code = TokenGenerator.getToken(45);
            user.setCode(code);
            userRepository.save(user);
            String authority = "http://localhost:8080";//todo move to props?
            String link = "/login/change-password/" + code;
            emailService.sendSimpleMessage(email.getEmail(), "Сброс пароля", "Для сброса пароля перейдите по ссылке " + authority + link);
            response.setResult(true);
        } else {
            response.setResult(false);
        }
        return response;
    }

    public GenericResponse changePassword(ChangePasswordRequest request) {
        User user = userRepository.getByCode(request.getCode());
        Map<String, String> errors = new HashMap<>();
        GenericResponse response = new GenericResponse();
        if (user != null){
            CaptchaCode captchaCode = captchaRepository.getBySecretCode(request.getCaptchaSecret()).orElseThrow(IllegalArgumentException::new);
            if (!captchaCode.getCode().equals(request.getCaptcha())){
                errors.put("captcha", "Код с картинки введён неверно");
            }
            if (request.getPassword().length() < 6){
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
            response.setErrors(errors);
        }
        return response;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        LoginResponse response = new LoginResponse();
        Authentication auth;
        try{
            auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        }catch (Exception e){
            response.setResult(false);
            return response;
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        response.setResult(true);
        UserDetailsImpl details = (UserDetailsImpl) auth.getPrincipal();
        User user = details.getUser();
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(user.getIsModerator() == 1? postRepository.getModeratedPostCount(user, ModerationType.NEW) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }

    public LoginResponse logout() {
        LoginResponse response = new LoginResponse();
        try{
            SecurityContextHolder.clearContext();
        }catch (Exception e){
            return response;
        }
        response.setResult(true);
        return response;
    }

    public LoginResponse checkUserStatus(Principal principal) {
        LoginResponse response = new LoginResponse();
        if (principal == null){
            response.setResult(false);
            return response;
        }
        response.setResult(true);
        User user = userRepository.getUserByEmail(principal.getName()).orElseThrow(() -> new UsernameNotFoundException(principal.getName() + " not found"));
        UserLoginResponse userLoginResponse = modelMapper.map(user, UserLoginResponse.class);
        userLoginResponse.setModeration(user.getIsModerator() == 1);
        userLoginResponse.setModerationCount(user.getIsModerator() == 1? postRepository.getModeratedPostCount(user, ModerationType.NEW) : 0);
        userLoginResponse.setSettings(user.getIsModerator() == 1);
        response.setUser(userLoginResponse);
        return response;
    }
}
