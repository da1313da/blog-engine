package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.exception.UserNotFoundException;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.interfaces.EditProfileService;
import com.example.projects.blogengine.utility.ImageSizeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class EditProfileServiceImpl implements EditProfileService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${max-photo-size}")
    private String maxPhotoSize;

    @Override
    public GenericResponse edit(MultipartFile photo, EditProfileRequest request, UserDetailsImpl userDetails) {
        User actualUser = userRepository.findById(userDetails.getUser().getId()).orElseThrow(UserNotFoundException::new);
        GenericResponse response = new GenericResponse();
        Map<String, String> errors = new HashMap<>();
        if (request.getName().matches("\\W")){
            errors.put("name", "Имя указанно неверно");
        } else if (userRepository.getUserByEmailNotEqual(request.getEmail(), userDetails.getUser().getId()).isPresent()){
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (request.getPassword() != null){
            if (request.getPassword().length() < 6){
                errors.put("password", "Пароль короче 6-ти символов");
            }
        }
        if (photo != null){
            long maxSize = ImageSizeConverter.getImageSize(maxPhotoSize);
            if (photo.getSize() > maxSize){
                errors.put("photo", "Фото слишком большое, нужно не более " + maxPhotoSize);
            }
        }
        if (errors.size() > 0 ){
            response.setErrors(errors);
            return response;
        } else {
            actualUser.setName(request.getName());
            actualUser.setEmail(request.getEmail());
            if (request.getPassword() != null){//double check :(
                actualUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (photo != null){
                //change photo
                try(InputStream inputStream = photo.getInputStream()){
                    BufferedImage bufferPhoto = ImageIO.read(inputStream);
                    BufferedImage resizedPhoto = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
                    resizedPhoto.createGraphics().drawImage(bufferPhoto, 0, 0, 36, 36, null);
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    ImageIO.write(resizedPhoto, "png", buffer);
                    byte[] byteArray = buffer.toByteArray();
                    String encodedImage = Base64.getEncoder().encodeToString(byteArray);
                    String photoString = "data:image/png;base64, " + encodedImage;
                    actualUser.setPhoto(photoString);
                }catch (IOException e){
                    //
                    return response;
                }
            }
            if (request.getRemovePhoto()!= null && request.getRemovePhoto() == 1){
                //remove photo
                actualUser.setPhoto("");
            }
            userRepository.save(actualUser);
        }
        response.setResult(true);
        return response;
    }
}
