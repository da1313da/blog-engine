package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.request.EditProfileRequest;
import com.example.projects.blogengine.api.response.*;
import com.example.projects.blogengine.security.UserDetailsImpl;
import com.example.projects.blogengine.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
public class ApiGeneralController {
    @Autowired
    private GlobalSettingsService globalSettingsService;
    @Autowired
    private TagService generalResponseService;
    @Autowired
    private ImageUploadService imageUploadService;
    @Autowired
    private EditProfileService editProfileService;
    @Autowired
    private BlogStatisticService statisticService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private GlobalInfoService globalInfoService;

    @GetMapping("/api/init")
    public GeneralInfoResponse getGeneralInfo(){
        return globalInfoService.getGlobalInfo();
    }

    @GetMapping("/api/settings")
    public ResponseEntity<?> getGlobalSettings(){
        Map<String, Boolean> response = globalSettingsService.getSettings();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user:moderate')")
    @PutMapping("/api/settings")
    public ResponseEntity<?> getGlobalSettings(@RequestBody Map<String, Boolean> request){
        globalSettingsService.setSettings(request);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public String addImage(@RequestParam("image") MultipartFile file){
        return imageUploadService.upload(file);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/profile/my", consumes = {"multipart/form-data"})
    public GenericResponse editProfile1(@RequestParam("photo") MultipartFile photo,
                                       @RequestParam String name,
                                       @RequestParam String email,
                                       @RequestParam int removePhoto,
                                       @RequestParam(required = false) String password,
                                       @AuthenticationPrincipal UserDetailsImpl user){
        EditProfileRequest request = new EditProfileRequest(name, email, password, removePhoto);
        return editProfileService.edit(photo, request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping(value = "/api/profile/my", consumes = {"application/json"})
    public GenericResponse editProfile2(@RequestBody EditProfileRequest request,
                                       @AuthenticationPrincipal UserDetailsImpl user){
        return editProfileService.edit(null, request, user);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/statistics/my")
    public StatisticResponse getUserStatistic(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return statisticService.getUser(userDetails);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @GetMapping("/api/statistics/all")
    public StatisticResponse getGlobalStatistic(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return statisticService.getBlog(userDetails);
    }
}
