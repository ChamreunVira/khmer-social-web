package com.kh.chamreunvira.springboot.service;

import com.kh.chamreunvira.springboot.dto.CommentRequest;
import com.kh.chamreunvira.springboot.model.Comment;

import com.kh.chamreunvira.springboot.dto.CommentResponse;
import java.util.List;

public interface CommentService {
     Comment create(Long postId ,CommentRequest request);
     Comment update(Long commentId , CommentRequest request);
     void delete(Long commentId);
     List<CommentResponse> getCommentsByPostId(Long postId);
}
