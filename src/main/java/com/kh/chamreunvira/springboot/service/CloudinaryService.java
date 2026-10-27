package com.kh.chamreunvira.springboot.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String upload(MultipartFile file) throws IOException {

        Map<String, Object> options = new HashMap<>();
        options.put("folder", "social/posts");

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                options
        );

        return result.get("secure_url").toString();
    }

    public void delete(String publicId) throws IOException {

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }

}
