package com.ddimitko.personal.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;

    //Authorization
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    //Personal
    @NotNull
    @Column(unique = true, nullable = false)
    private String userTag;

    private String fullName;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_picture_id")
    private Picture profilePicture;

    @OneToMany(mappedBy = "user")
    private List<Post> postList;

    @OneToMany
    private List<Group> groupList;


}
