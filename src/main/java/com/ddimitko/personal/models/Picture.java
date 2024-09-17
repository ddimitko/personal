package com.ddimitko.personal.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "pictures")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pictureId;

    @Column(name = "image_data")
    private byte[] imageData;

}
