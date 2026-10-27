package com.kh.chamreunvira.springboot.service.impl;

import com.kh.chamreunvira.springboot.dto.ProfileResponse;
import com.kh.chamreunvira.springboot.dto.UpdateProfileRequest;
import com.kh.chamreunvira.springboot.model.User;
import com.kh.chamreunvira.springboot.repository.UserRepository;
import com.kh.chamreunvira.springboot.security.AuthService;
import com.kh.chamreunvira.springboot.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    public ProfileResponse getProfile() {
        User user = authService.getCurrentUser();
        return toResponse(user);
    }

    @Override
    public ProfileResponse getByNickname(String nickname) {
        String cleanNickname = nickname.trim();
        if (cleanNickname.startsWith("@")) {
            cleanNickname = cleanNickname.substring(1);
        }
        Optional<User> userOpt = userRepository.findByNickname("@" + cleanNickname);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByNickname(cleanNickname);
        }
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(cleanNickname);
        }
        if (userOpt.isEmpty()) {
            try {
                Long id = Long.parseLong(cleanNickname);
                userOpt = userRepository.findById(id);
            } catch (NumberFormatException ignored) {}
        }
        User user = userOpt.orElseThrow(() -> new RuntimeException("User not found with nickname: " + nickname));
        return toResponse(user);
    }

    @Override
    public ProfileResponse update(UpdateProfileRequest req) {
        User user = authService.getCurrentUser();
        user.setUsername(req.getUsername());
        user.setNickname(req.getNickname());
        user.setBio(req.getBio());
        user.setDob(req.getDob());
        user.setPhone(req.getPhone());
        user.setGender(req.getGender());
        user.setAddress(req.getAddress());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }


    private ProfileResponse toResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .profile(user.getProfile())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .bio(user.getBio())
                .dob(user.getDob())
                .gender(user.getGender())
                .address(user.getAddress())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
