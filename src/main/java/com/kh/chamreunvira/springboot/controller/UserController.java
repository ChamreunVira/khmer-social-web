package com.kh.chamreunvira.springboot.controller;

import com.kh.chamreunvira.springboot.data.ApiResponse;
import com.kh.chamreunvira.springboot.dto.UpdateProfileRequest;
import com.kh.chamreunvira.springboot.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kh.chamreunvira.springboot.dto.ProfileResponse;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        return ResponseEntity.ok().body(ApiResponse.success("Success" , userService.getProfile()));
    }

    @GetMapping("/by-nickname/{nickname}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok().body(ApiResponse.success("Success", userService.getByNickname(nickname)));
    }

    @GetMapping("/profile/{nickname}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByNicknamePath(@PathVariable String nickname) {
        return ResponseEntity.ok().body(ApiResponse.success("Success", userService.getByNickname(nickname)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = userService.update(request);
        return ResponseEntity.ok().body(ApiResponse.success("Update profile successfully" , response));
    }

}
