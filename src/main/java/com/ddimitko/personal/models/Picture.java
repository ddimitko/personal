package com.ddimitko.personal.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Data
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pictureId;

    private String description;

    @Column(name = "image_data", columnDefinition="bytea")
    private byte[] imageData;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

}
