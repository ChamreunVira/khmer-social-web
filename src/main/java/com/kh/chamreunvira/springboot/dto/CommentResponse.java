package com.kh.chamreunvira.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private LocalDate createAt;
    private Long userId;
    private String username;
    private String nickname;
    private String profile;

}
