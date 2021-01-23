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
        String displayCode = tokenGenerator.getToken(blogProperties.getCaptcha().getDisplayCodeLength());
        String databaseSecretCode = tokenGenerator.getToken(blogProperties.getCaptcha().getSecretCodeLength());

        BufferedImage imageWithCode = createImageByCode(displayCode);
        BufferedImage resizedImageWithCode = resizeImage(imageWithCode,
                blogProperties.getCaptcha().getCaptchaImageWidth(), blogProperties.getCaptcha().getCaptchaImageHeight());
        String base64imageWithCode = convertToBase64(resizedImageWithCode);

        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setCode(displayCode);
        captchaCode.setSecretCode(databaseSecretCode);
        captchaRepository.save(captchaCode);

        return getResponse(base64imageWithCode, databaseSecretCode);
    }

    private BufferedImage createImageByCode(String code){
        GCage cage = new GCage();
        return cage.drawImage(code);
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height){
        BufferedImage sizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        sizedImage.createGraphics().drawImage(image, 0,0, width, height, null);
        return sizedImage;
    }

    private String convertToBase64(BufferedImage image){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", buffer);
        } catch (IOException e) {
            InternalException internalException = new InternalException("Captcha save io error!", HttpStatus.BAD_REQUEST);
            internalException.setException(e);
            throw internalException;
        }
        byte[] byteArray = buffer.toByteArray();
        String encodedImage = Base64.getEncoder().encodeToString(byteArray);
        return  "data:image/png;base64, " + encodedImage;
    }

    private CaptchaResponse getResponse(String base64Image, String databaseSecretCode){
        CaptchaResponse response = new CaptchaResponse();
        response.setImage(base64Image);
        response.setSecret(databaseSecretCode);
        return response;
    }
}
