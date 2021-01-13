package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.config.BlogProperties;
import com.example.projects.blogengine.exception.InternalException;
import com.example.projects.blogengine.exception.NotFoundException;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class EditProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlogProperties properties;

    public GenericResponse edit(MultipartFile photo, EditProfileRequest request, UserDetailsImpl userDetails) {
        User actualUser = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User " + userDetails.getUser() + " not found!", HttpStatus.BAD_REQUEST));
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = validateRequest(photo, request, userDetails);
        if (errors.size() > 0 ){
            response.setErrors(errors);
            return response;
        } else {
            actualUser.setName(request.getName());
            actualUser.setEmail(request.getEmail());
            if (request.getPassword() != null){
                actualUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (photo != null){
                String photoString = convertPhotoToBase64(photo);
                actualUser.setPhoto(photoString);
            }
            if (request.getRemovePhoto()!= null && request.getRemovePhoto() == 1){
                actualUser.setPhoto("");
            }
            userRepository.save(actualUser);
        }
        response.setResult(true);
        return response;
    }

    private Map<String, String> validateRequest(MultipartFile photo, EditProfileRequest request, UserDetailsImpl userDetails){
        Map<String, String> errors = new HashMap<>();
        if (request.getEmail() == null || request.getEmail().isEmpty()){
            errors.put("email", "Email не должен быть пустым");
        }
        if (request.getName().matches("\\W")){
            errors.put("name", "Имя указанно неверно");
        }
        if (userRepository.getUserByEmailNotEqual(request.getEmail(), userDetails.getUser().getId()).isPresent()){
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (request.getPassword() != null){
            if (request.getPassword().length() < properties.getAccount().getPasswordLength()){
                errors.put("password", "Пароль короче 6-ти символов");
            }
        }
        if (photo != null){
            long maxSize = properties.getUpload().getMaxPhotoSize().toBytes();
            if (photo.getSize() > maxSize){
                errors.put("photo", "Фото слишком большое, нужно не более " + maxSize + " MB");
            }
        }
        return errors;
    }

    private String convertPhotoToBase64(MultipartFile photo){
        try(InputStream inputStream = photo.getInputStream()){
            BufferedImage bufferPhoto = ImageIO.read(inputStream);
            BufferedImage resizedPhoto = new BufferedImage(
                    properties.getAccount().getAvatarImageWidth(),
                    properties.getAccount().getAvatarImageHeight(),
                    BufferedImage.TYPE_INT_RGB);
            resizedPhoto.createGraphics().drawImage(
                    bufferPhoto,
                    0,
                    0,
                    properties.getAccount().getAvatarImageWidth(),
                    properties.getAccount().getAvatarImageHeight(),
                    null);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ImageIO.write(resizedPhoto, "png", buffer);
            byte[] byteArray = buffer.toByteArray();
            String encodedImage = Base64.getEncoder().encodeToString(byteArray);
            return "data:image/png;base64, " + encodedImage;
        }catch (IOException e){
            throw new InternalException("An Error occurred during photo upload!", HttpStatus.BAD_REQUEST);
        }
    }
}
