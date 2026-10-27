package com.kh.chamreunvira.springboot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post extends BaseEntity<Long , LocalDate> {

    @Column(name = "title" , length = 150 , nullable = false)
    private String title;
    @Column(name = "content" , length = 1000 , nullable = false)
    private String content;

    @Column(name = "image")
    private String image;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tbl_post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url", length = 2000)
    @OrderColumn(name = "position")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false, referencedColumnName = "id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "post" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments;

    @OneToMany(mappedBy = "post" , fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Like> likes;


}
