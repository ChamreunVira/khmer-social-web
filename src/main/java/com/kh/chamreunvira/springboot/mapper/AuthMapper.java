package com.kh.chamreunvira.springboot.mapper;

import com.kh.chamreunvira.springboot.dto.UserRequest;
import com.kh.chamreunvira.springboot.dto.UserResponse;
import com.kh.chamreunvira.springboot.enumz.Role;
import com.kh.chamreunvira.springboot.model.User;
import com.kh.chamreunvira.springboot.service.CloudinaryService;
import com.kh.chamreunvira.springboot.utils.FileUtil;
import com.kh.chamreunvira.springboot.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthMapper {

    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    public UserResponse mapperToResponse(User user , String token) {
        return UserResponse
                .builder()
                .id(user.getId())
                .username(user.getFullName())
                .nickname(user.getNickname())
                .profile(user.getProfile())
                .email(user.getUsername())
                .token(token)
                .role(user.getRoles().stream().map(Enum::name).toList())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User mapperToEntity(UserRequest req) throws Exception {
        MultipartFile file = req.getProfile();
        String imageName = null;
        if(file != null && !file.isEmpty()) {
            imageName = cloudinaryService.upload(file);
        }
        return User
                .builder()
                .username(req.getUsername())
                .profile(imageName)
                .nickname(Utils.toNickName(req.getUsername()))
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(List.of(Role.USER))
                .build();
    }

}
