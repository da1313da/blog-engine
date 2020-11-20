package com.example.projects.blogengine.service;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.ErrorsResponse;
import com.example.projects.blogengine.exception.UserNotFoundException;
import com.example.projects.blogengine.model.User;
import com.example.projects.blogengine.repository.UserRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.interfaces.EditProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Override
    public ErrorsResponse edit(EditProfileRequest request, UserDetailsImpl userDetails) {
        User actualUser = userRepository.findById(userDetails.getUser().getId()).orElseThrow(UserNotFoundException::new);
        ErrorsResponse response = new ErrorsResponse();
        Map<String, String> errors = new HashMap<>();
        if (request.getName().matches("\\W")){
            errors.put("name", "Имя указанно неверно");
        } else if (userRepository.getUserByEmail(request.getEmail()).isPresent()){
            errors.put("email", "Этот e-mail уже зарегистрирован");
        }
        if (request.getPassword() != null){
            if (request.getPassword().length() < 6){
                errors.put("password", "Пароль короче 6-ти символов");
            }
        }
        if (request.getPhoto() != null){
            if (request.getPhoto().getSize() > 5 * 1024 * 1024){//props!
                errors.put("photo", "Фото слишком большое, нужно не более 5 Мб");
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
            if (request.getPhoto() != null){
                //change photo
                try(InputStream inputStream = request.getPhoto().getInputStream()){
                    BufferedImage photo = ImageIO.read(inputStream);
                    BufferedImage resizedPhoto = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
                    resizedPhoto.createGraphics().drawImage(photo, 0, 0, 36, 36, null);
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
