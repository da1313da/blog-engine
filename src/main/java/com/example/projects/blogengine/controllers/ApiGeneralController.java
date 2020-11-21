package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.CalendarResponse;
import com.example.projects.blogengine.api.response.GeneralInfoResponse;
import com.example.projects.blogengine.api.response.GenericResponse;
import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.GeneralResponseService;
import com.example.projects.blogengine.service.interfaces.CalendarService;
import com.example.projects.blogengine.service.interfaces.EditProfileService;
import com.example.projects.blogengine.service.interfaces.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {

    @Autowired
    private GeneralInfoResponse generalInfo;
    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;
    @Autowired
    private GeneralResponseService generalResponseService;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private EditProfileService editProfileService;


    @Autowired
    //@Qualifier("calendarServiceJavaSide")
    @Qualifier("calendarServiceDbSide")
    private CalendarService calendarService;

    @GetMapping("/api/init")
    public GeneralInfoResponse getGeneralInfo(){
        return generalInfo;
    }

    @GetMapping("/api/settings")
    public ResponseEntity<?> getGlobalSettings(){
        List<GlobalSettings> settings = (List<GlobalSettings>) globalSettingsRepository.findAll();
        Map<String, String> responseBody = new HashMap<>();
        for (GlobalSettings s : settings) {
            responseBody.put(s.getCode(), s.getValue());
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    public TagsListResponse getTagList(@RequestParam(name = "query", required = false) String query){
        return generalResponseService.getTagList(query);
    }

    @GetMapping("/api/calendar")
    public CalendarResponse getCalendar(@RequestParam(name = "year", required = false) Integer year){
        if (year == null) year = ZonedDateTime.now(ZoneId.of("UTC")).getYear();
        return calendarService.getCalendarResponse(year);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/image", consumes = {"multipart/form-data"})
    public ResponseEntity<Object> addImage(@RequestParam("image") MultipartFile file, @AuthenticationPrincipal UserDetailsImpl user){
        return ResponseEntity.ok(imageUploadService.upload(user, file));
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/profile/my", consumes = {"multipart/form-data", "application/json"})
    public GenericResponse editProfile(@RequestBody EditProfileRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl user){
        return editProfileService.edit(request, user);
    }
}
