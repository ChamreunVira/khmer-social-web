package com.kh.chamreunvira.springboot.repository;

import com.kh.chamreunvira.springboot.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post , Long> {
    List<Post> findByUserId(Long userId);
}
