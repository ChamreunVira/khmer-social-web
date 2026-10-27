package com.kh.chamreunvira.springboot.dto;

import com.kh.chamreunvira.springboot.enumz.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private String username;
    private String profile;
    private String email;
    private String nickname;
    private String bio;
    private LocalDate dob;
    private String phone;
    private String gender;
    private String address;
    private List<Role> roles;

    private LocalDate createdAt;
    private LocalDate updatedAt;
}