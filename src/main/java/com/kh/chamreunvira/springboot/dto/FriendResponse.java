package com.kh.chamreunvira.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String profile;

}
