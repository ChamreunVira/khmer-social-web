package com.kh.chamreunvira.springboot.service;

import com.kh.chamreunvira.springboot.dto.ProfileResponse;
import com.kh.chamreunvira.springboot.dto.UpdateProfileRequest;

public interface UserService {

    ProfileResponse getProfile();

    ProfileResponse getByNickname(String nickname);

    ProfileResponse update(UpdateProfileRequest req);

}
