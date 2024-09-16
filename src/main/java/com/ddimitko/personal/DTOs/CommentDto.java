package com.ddimitko.personal.DTOs;

import lombok.Data;

import java.text.SimpleDateFormat;

@Data
public class CommentDto {

    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private String content;
    private String createdAt;

    private String userTag;

}
