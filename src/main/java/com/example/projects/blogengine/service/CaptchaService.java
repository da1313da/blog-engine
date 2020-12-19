package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.CaptchaResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.InternalException;
import com.example.projects.blogengine.model.CaptchaCode;
import com.example.projects.blogengine.repository.CaptchaRepository;
import com.example.projects.blogengine.utility.TokenGenerator;
import com.github.cage.GCage;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class CaptchaService {

    private final TokenGenerator tokenGenerator;
    private final BlogProperties blogProperties;
    private final CaptchaRepository captchaRepository;

    public CaptchaResponse captcha() {
        GCage cage = new GCage();
        CaptchaResponse response = new CaptchaResponse();
        String secret = tokenGenerator.getToken(blogProperties.getCaptcha().getSecretCodeLength());
        String code = tokenGenerator.getToken(blogProperties.getCaptcha().getDisplayCodeLength());
        BufferedImage baseImage = cage.drawImage(code);
        BufferedImage sizedImage = new BufferedImage(
                blogProperties.getCaptcha().getCaptchaImageWidth(),
                blogProperties.getCaptcha().getCaptchaImageHeight(),
                BufferedImage.TYPE_INT_RGB);
        sizedImage.createGraphics().drawImage(
                baseImage,
                0,
                0,
                blogProperties.getCaptcha().getCaptchaImageWidth(),
                blogProperties.getCaptcha().getCaptchaImageHeight(),
                null);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ImageIO.write(sizedImage, "png", buffer);
        } catch (IOException e) {
            InternalException internalException = new InternalException("Captcha save io error!", HttpStatus.BAD_REQUEST);
            internalException.setException(e);
            throw internalException;
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
}
