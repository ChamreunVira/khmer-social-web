package com.kh.chamreunvira.springboot.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(min = 3, max = 50)
    private String username;

    @Pattern(
            regexp = "^@[A-Za-z0-9_]{2,30}$",
            message = "Nickname must start with @ and contain only letters, numbers, and underscores"
    )
    private String nickname;

    @Size(max = 500)
    private String bio;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Pattern(
            regexp = "^[MF]$",
            message = "Gender must be M or F"
    )
    private String gender;

    @Size(max = 255)
    private String address;
}
