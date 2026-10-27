package com.kh.chamreunvira.springboot.mapper;

import com.kh.chamreunvira.springboot.dto.*;
import com.kh.chamreunvira.springboot.model.Comment;
import com.kh.chamreunvira.springboot.model.Post;
import com.kh.chamreunvira.springboot.model.User;
import com.kh.chamreunvira.springboot.repository.LikeRepository;
import com.kh.chamreunvira.springboot.security.AuthService;
import com.kh.chamreunvira.springboot.service.CloudinaryService;
import com.kh.chamreunvira.springboot.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final AuthService authService;
    private final LikeRepository likeRepository;
    private final CloudinaryService cloudinaryService;

    public Post mapperToEntity(PostRequest req , Long userId) throws Exception {
        Post post = new Post();
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        List<MultipartFile> files = new ArrayList<>();
        if (req.getFiles() != null) {
            files.addAll(req.getFiles().stream().filter(file -> file != null && !file.isEmpty()).toList());
        }
        if (files.isEmpty() && req.getFile() != null && !req.getFile().isEmpty()) {
            files.add(req.getFile());
        }
        if (!files.isEmpty()) {
            List<String> uploadedFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                uploadedFiles.add(cloudinaryService.upload(file));
            }
            post.setImages(uploadedFiles);
            post.setImage(uploadedFiles.getFirst());
        }
        User user = authService.fetchUserById(userId);
        post.setUser(user);
        return post;
    }

    public List<CommentResponse> mapperToResponseFromComment(List<Comment> comments) {
        return comments
                .stream().map(comment -> CommentResponse
                        .builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .userId(comment.getUser().getId())
                        .username(comment.getUser().getFullName())
                        .nickname(comment.getUser().getNickname())
                        .profile(comment.getUser().getProfile())
                        .createAt(comment.getCreateAt())
                        .build())
                .toList();
    }

    public PostResponse mapperToResponse(Post post , Long userId) {

        PostResponse response = new PostResponse();
        AuthorResponse authorResponse = new AuthorResponse();
        authorResponse.setUserId(post.getUser().getId());
        authorResponse.setUsername(post.getUser().getFullName());
        authorResponse.setNickname(post.getUser().getNickname());
        authorResponse.setProfile(post.getUser().getProfile());

        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setImage(post.getImage());
        response.setImages(postImages(post));
        response.setAuthorResponse(authorResponse);

        List<CommentResponse> commentResponses = mapperToResponseFromComment(post.getComments());
        response.setComments(commentResponses);
        int totalLikes = likeRepository.countByPostId(response.getId());
        boolean likeByMe = likeRepository.existsByPostIdAndUserId(response.getId() , userId);
        response.setLikeCount(totalLikes);
        response.setLikeByMe(likeByMe);
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        return response;
    }

    public List<PostResponse> mapperToList(List<Post> posts , Long userId) {
        return posts.stream().map(p -> {
            AuthorResponse authorResponse = new AuthorResponse();
            authorResponse.setUserId(p.getUser().getId());
            authorResponse.setUsername(p.getUser().getFullName());
            authorResponse.setNickname(p.getUser().getNickname());
            authorResponse.setProfile(p.getUser().getProfile());
            int totalLikes = likeRepository.countByPostId(p.getId());
            boolean likeByMe = likeRepository.existsByPostIdAndUserId(p.getId() , userId);
            return PostResponse.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .content(p.getContent())
                    .image(p.getImage())
                    .images(postImages(p))
                    .authorResponse(authorResponse)
                    .comments(mapperToResponseFromComment(p.getComments()))
                    .likeCount(totalLikes)
                    .likeByMe(likeByMe)
                    .createdAt(p.getCreatedAt())
                    .updatedAt(p.getUpdatedAt())
                   .build();
        }).toList();
    }

    private List<String> postImages(Post post) {
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            return post.getImages();
        }
        return post.getImage() == null || post.getImage().isBlank() ? List.of() : List.of(post.getImage());
    }


}
