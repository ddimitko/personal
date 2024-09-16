package com.ddimitko.personal.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    //Relations
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    /*@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userTag")
    @JsonIdentityReference(alwaysAsId = true)*/
    private User user;

    @NotNull
    private String content;
    private String imageUrl;

    @CreationTimestamp
    private Instant createdAt;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

}
