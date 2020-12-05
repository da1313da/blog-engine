package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.interfaces.ImageUploadService;
import com.example.projects.blogengine.utility.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    private final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogProperties properties;

    @Override
    public Object upload(UserDetailsImpl user, MultipartFile file) {
        long imageSize = properties.getUpload().getMaxImageSize().toBytes();
        if (file.getSize() > imageSize) throw new MaxUploadSizeExceededException(file.getSize());
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = new HashMap<>();
        try(InputStream is = file.getInputStream()){
            Path uploadFolder = Paths.get(properties.getUpload().getLocation());
            if (!Files.exists(uploadFolder)) {
                Files.createDirectories(uploadFolder);
            }
            if (file.getContentType() != null && (file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png"))){//todo npe?
                String subDirName = TokenGenerator.getToken(6);
                Path subDir = Paths.get(subDirName.substring(0, 2))
                        .resolve(Paths.get(subDirName.substring(2, 4)))
                        .resolve(Paths.get(subDirName.substring(4, 6)));
                Path randomDir = uploadFolder.resolve(subDir);
                if (!Files.exists(randomDir)){
                    Files.createDirectories(randomDir);
                }
                Path fullPath = file.getContentType().contains("jpeg") ?
                        randomDir.resolve(Paths.get(TokenGenerator.getToken(5) + ".jpg")) :
                        randomDir.resolve(Paths.get(TokenGenerator.getToken(5) + ".png"));
                Files.copy(is, fullPath);
                return fullPath.toString();
            } else {
                response.setResult(false);
                errors.put("image", "Тип изображения не поддерживается");
                response.setErrors(errors);
                return response;
            }
        } catch (IOException e) {
            logger.info(e.toString());
            response.setResult(false);
            errors.put("image", "Ошибка при загрузке изображения");
            response.setErrors(errors);
            return response;
        }
    }
}
