package com.example.projects.blogengine.service;

import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.ImageIOException;
import com.example.projects.blogengine.utility.TokenGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class ImageUploadService {

    private final BlogProperties blogProperties;
    private final TokenGenerator tokenGenerator;

    public String upload(MultipartFile file) {
        long imageSize = blogProperties.getUpload().getMaxImageSize().toBytes();
        if (file.getSize() > imageSize) {
            throw new ImageIOException("File size too large " + file.getSize() +
                    "!. Maximum size: " + blogProperties.getUpload().getMaxImageSize(), HttpStatus.BAD_REQUEST);
        }
        try(InputStream is = file.getInputStream()){
            Path uploadFolder = Paths.get(blogProperties.getUpload().getLocation());
            if (!Files.exists(uploadFolder)) {
                Files.createDirectories(uploadFolder);
            }
            if (file.getContentType() != null
                    && (file.getContentType().equals("image/jpeg")
                    || file.getContentType().equals("image/png"))){
                String subDirName = tokenGenerator.getToken(6);
                Path subDir = Paths.get(subDirName.substring(0, 2))
                        .resolve(Paths.get(subDirName.substring(2, 4)))
                        .resolve(Paths.get(subDirName.substring(4, 6)));
                Path randomDir = uploadFolder.resolve(subDir);
                if (!Files.exists(randomDir)){
                    Files.createDirectories(randomDir);
                }
                Path fullPath = file.getContentType().contains("jpeg") ?
                        randomDir.resolve(Paths.get(tokenGenerator.getToken(5) + ".jpg")) :
                        randomDir.resolve(Paths.get(tokenGenerator.getToken(5) + ".png"));
                Files.copy(is, fullPath);
                return fullPath.toString();
            } else {
                throw new ImageIOException("Unsupported image type!", HttpStatus.BAD_REQUEST);
            }
        } catch (IOException e) {
            throw new ImageIOException("Error while loading image!", HttpStatus.BAD_REQUEST);
        }
    }
}
